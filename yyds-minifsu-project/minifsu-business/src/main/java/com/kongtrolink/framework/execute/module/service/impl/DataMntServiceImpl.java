package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.dao.ConfigDao;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import com.kongtrolink.framework.execute.module.dao.RunStateDao;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.model.RunState;
import com.kongtrolink.framework.execute.module.model.SignalModel;
import com.kongtrolink.framework.execute.module.model.SignalType;
import com.kongtrolink.framework.execute.module.service.DataMntService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by mystoxlol on 2019/4/10, 18:57.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class DataMntServiceImpl implements DataMntService {
    Logger logger = LoggerFactory.getLogger(DataMntServiceImpl.class);
    @Value("${rpc.controller.hostname}")
    private String controllerHost;
    @Value("${rpc.controller.port}")
    private int controllerPort;
    @Value("${server.bindIp}")
    private String host;
    @Value("${server.rpc.port}")
    private String port;
    @Value("${server.name}")
    private String name;
    @Autowired
    private LogDao logDao;
    @Autowired
    RedisUtils redisUtils;

    @Autowired
    DeviceDao deviceDao;

    @Autowired
    private RunStateDao runStateDao;

    private ConfigDao configDao;

    @Autowired
    public void setConfigDao(ConfigDao configDao) {
        this.configDao = configDao;
    }

    private RpcModule rpcModule;

    @Autowired
    public void setRpcModule(RpcModule rpcModule) {
        this.rpcModule = rpcModule;
    }

    @Override
    public JSONObject getSignalList(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject searchCondition = moduleMsg.getPayload();
        String devId = (String) searchCondition.get("deviceId");
        Integer resNo = (Integer) searchCondition.get("resNo");
        Integer type = (Integer) searchCondition.get("type");

        String dev = type + "-" + resNo;
        //获取实时数据的key
        Set<String> mntData = redisUtils.getHkeys(RedisHashTable.SN_DATA_HASH + sn, dev + "_*");
        JSONArray jsonArray = new JSONArray();
        for (String key : mntData) { //通过key获取实时数据
            JSONObject coData = new JSONObject();
            Object value = redisUtils.getHash(RedisHashTable.SN_DATA_HASH + sn, key);
            String coId = key.replaceFirst(sn + "_" + dev + "_", "");
            coData.put("coId", coId);
            SignalModel signalModel = configDao.findSignalModelByDeviceTypeAndCoId(type, coId);
            Integer valueBase = signalModel == null ? 1 : signalModel.getValueBase();
            double value1 = Double.valueOf(value + "");
            coData.put("value", value1/valueBase);
            JSONObject data = tranceDataId(type, coId); //数据点翻译
            coData.putAll(data);
            jsonArray.add(coData);
        }
        JSONObject result = new JSONObject();
        result.put("dev", dev);
        result.put("info", jsonArray);
        return result;
    }

    /**
     * 转换数据点id类型名称
     *
     * @param coId
     * @return
     */
    private JSONObject tranceDataId(Integer devType, String coId) {
        int coIdLen = coId.length();
        if (coIdLen < 6) {
            coId = String.format("%6s", coId).replaceAll("\\s", "0");
        }
        if (coIdLen > 6) {
            coId = coId.substring(coIdLen - 6);
        }
        JSONObject data = new JSONObject();
        String coIdType = coId.substring(0, 1);
        data.put("dataType", SignalType.toName(coIdType));
        SignalModel signalModel = configDao.findSignalModelByDeviceTypeAndCoId(devType, coId);
        data.put("name", signalModel != null ? signalModel.getName() : null);
        data.put("unit", signalModel != null ? signalModel.getUnit() : null);
        data.put("type", signalModel != null ? signalModel.getType() : null);
        return data;

    }

    @Override
    public JSONObject setThreshold(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject alarmSignal = moduleMsg.getPayload();
        String deviceId = (String) alarmSignal.get("deviceId");
        String coId = (String) alarmSignal.get("coId");
        String configId = (String) alarmSignal.get("configId");
        Double threshold = alarmSignal.get("threshold") == null ? null : Double.parseDouble(alarmSignal.get("threshold") + "");
        Float hystersis = alarmSignal.get("hystersis") == null ? null : Float.parseFloat(alarmSignal.get("hystersis") + "");
        Integer relativeval = alarmSignal.get("relativeval") == null ? null : (Integer) alarmSignal.get("relativeval");
        Integer level = alarmSignal.get("level") == null ? null : (Integer) alarmSignal.get("level");
        Integer delay = alarmSignal.get("delay") == null ? null : (Integer) alarmSignal.get("delay");

        Device device = deviceDao.findDeviceById(deviceId);
        int devType = device.getType();
        int resNo = device.getResNo();
        String dev = devType + "-" + resNo; //设备
        String alarmConfigKey = sn + "_" + dev + "_" + coId;//redis 告警配置键值

        //设置内存值
        JSONArray redisSignalObj = redisUtils.getHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, alarmConfigKey, JSONArray.class);
        List<AlarmSignalConfig> alarmSignals = JSONArray.parseArray(redisSignalObj.toString(), AlarmSignalConfig.class);

        for (AlarmSignalConfig alarmSignalConfig : alarmSignals) {
            String coId1 = alarmSignalConfig.getCoId();
            if (coId1.equals(coId)) {
                alarmSignalConfig.setThreshold(threshold);
            }
        }

        redisUtils.setHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, alarmConfigKey, alarmSignals);
        //设置数据库值
        AlarmSignalConfig alarmSignalConfig = configDao.findAlarmSignalConfigById(configId);
        if (threshold != null)
            alarmSignalConfig.setThreshold(threshold);
        if (relativeval != null)
            alarmSignalConfig.setRecoverDelay(relativeval);
        if (hystersis != null)
            alarmSignalConfig.setHystersis(hystersis);
        if (level != null)
            alarmSignalConfig.setLevel(level);
        if (delay != null)
            alarmSignalConfig.setDelay(delay);

        configDao.saveAlarmSignalConfig(alarmSignalConfig);

        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }

    @Override
    public JSONArray getThreshold(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject payload = moduleMsg.getPayload();
        Integer devType = (Integer) payload.get("type");
        Integer resNo = (Integer) payload.get("resNo");
        String port = (String) payload.get("port");
        Device device = deviceDao.findDeviceByTypeResNoPort(sn, devType, resNo, port); //根据信号点的devType获取deviceId
        String coId = (String) payload.get("coId");
        String alarmId = (String) payload.get("alarmId");
        List<AlarmSignalConfig> alarmSignalConfigList = configDao.findAlarmSignalConfigByDeviceIdAndCoId(device.getId(), coId,alarmId); //根据deviceId和数据点id获取信号点配置列表
        return (JSONArray) JSONArray.toJSON(alarmSignalConfigList);
    }

    @Override
    public JSONObject setData(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject payload = moduleMsg.getPayload();
        String dev = (String) payload.get("dev");
        Integer devType = Integer.parseInt(dev.split("-")[0]);
        String coId = payload.get("stePoint") + "";
        Integer data = (Integer) payload.get("steData");
        SignalModel signalModel = configDao.findSignalModelByDeviceTypeAndCoId(devType, coId);
        Integer valueBase = signalModel == null ? 1 : signalModel.getValueBase();
        JSONObject terminalPayload = payload;
        terminalPayload.put("data", data * valueBase);

        try {
            // 向终端设置信号点值报文
            moduleMsg.setPktType(PktType.SET_DATA_TERMINAL);
            moduleMsg.setPayload(terminalPayload);
            RpcNotifyProto.RpcMessage rpcMessage = rpcModule.postMsg(moduleMsg.getMsgId(),
                    new InetSocketAddress(controllerHost, controllerPort),
                    JSONObject.toJSONString(moduleMsg));
            if (!RpcNotifyProto.MessageType.ERROR.equals(rpcMessage.getType())) {
                JSONObject result = new JSONObject();
                result.put("result", 1);
                return result;
            }
        } catch (IOException e) {
            logger.error("set_data error .." + e.toString());
            //日志记录
            Log log = new Log();
            log.setErrorCode(StateCode.CONNECT_ERROR);
            log.setSN(sn);
            log.setMsgType(moduleMsg.getPktType());
            log.setMsgId(moduleMsg.getMsgId());
            log.setHostName(host);
            log.setServiceName(name);
            log.setTime(new Date(System.currentTimeMillis()));
            logDao.saveLog(log);
        }
        JSONObject result = new JSONObject();
        result.put("result", 0);
        return result;
    }



    @Override
    public JSONObject saveRunStatus(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject payload = moduleMsg.getPayload();
        RunState runState = new RunState();
        runState.setSn(sn);
        runState.setCpuUse((String) payload.get("cpuUse"));
        runState.setMemUse((String) payload.get("memUse"));
        runState.setSysTime((Long.parseLong(payload.get("sysTime") + "")));
        runState.setCsq((Integer) payload.get("csq"));
        runState.setCreateTime(new Date());
        runStateDao.saveRunState(runState);
        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }

    @Override
    public JSONObject saveSignalModel(ModuleMsg moduleMsg) {
        JSONArray jsonArray = moduleMsg.getArrayPayload();

        List<SignalModel> signalModels = JSONArray.parseArray(jsonArray.toJSONString(), SignalModel.class);
        for (SignalModel signalModel : signalModels) {
            SignalModel signalModel1 = configDao.findSignalModelByDeviceTypeAndCoId(signalModel.getDeviceType(), signalModel.getDataId());
            if (signalModel1 != null) {
                BeanUtils.copyProperties(signalModel, signalModel1, "id");
                configDao.saveSignalModel(signalModel1);
            } else
                configDao.saveSignalModel(signalModel);
        }
        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }

}
