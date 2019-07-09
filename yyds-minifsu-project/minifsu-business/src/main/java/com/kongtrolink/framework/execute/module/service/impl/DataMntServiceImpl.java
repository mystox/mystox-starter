package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.dao.ConfigDao;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.dao.RunStateDao;
import com.kongtrolink.framework.execute.module.dao.TerminalDao;
import com.kongtrolink.framework.execute.module.model.*;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.service.DataMntService;
import com.kongtrolink.framework.execute.module.service.LogService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.*;

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
    @Value("${switch.threshold.engineVer}")
    private String engineVer;

    private final TerminalDao terminalDao;
    private final RedisUtils redisUtils;

    private final DeviceDao deviceDao;

    private final RunStateDao runStateDao;

    private ConfigDao configDao;

    private LogService logService;

    @Autowired
    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    @Autowired
    public DataMntServiceImpl(TerminalDao terminalDao, RedisUtils redisUtils, DeviceDao deviceDao, RunStateDao runStateDao, ThreadPoolTaskExecutor businessExecutor) {
        this.terminalDao = terminalDao;
        this.redisUtils = redisUtils;
        this.deviceDao = deviceDao;
        this.runStateDao = runStateDao;
        this.businessExecutor = businessExecutor;
    }

    @Autowired
    public void setConfigDao(ConfigDao configDao) {
        this.configDao = configDao;
    }

    private RpcModule rpcModule;

    @Autowired
    public void setRpcModule(RpcModule rpcModule) {
        this.rpcModule = rpcModule;
    }

    private final ThreadPoolTaskExecutor businessExecutor;

    @Override
    public JSONObject getSignalList(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject searchCondition = moduleMsg.getPayload();
        String devId = (String) searchCondition.get("deviceId");
        Integer resNo = (Integer) searchCondition.get("resNo");
        Integer type = (Integer) searchCondition.get("type");

        String dev = type + "-" + resNo;
        //获取实时数据的key
        String table = RedisHashTable.SN_DATA_HASH + sn;
        Set<String> mntData = redisUtils.getHkeys(table, dev + "_*");
        List<String> mntList = new ArrayList<>(mntData);
        List values = redisUtils.hashMultiGet(table, mntList, Integer.class);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < mntList.size(); i++) {
            String key = mntList.get(i);
            JSONObject coData = new JSONObject();
//            Object value = redisUtils.getHash(RedisHashTable.SN_DATA_HASH + sn, key);
            Integer value = new Integer(0);
            Object o = values.get(i);
            if (o instanceof BigDecimal) {
                BigDecimal b = (BigDecimal) o;
                value = b.intValue();
            } else if (o instanceof Integer)
            {
                value = (Integer) o;
            }
            String coId = key.replaceFirst(dev + "_", "");
            coData.put("coId", coId);
            SignalModel signalModel = configDao.findSignalModelByDeviceTypeAndCoId(type, coId);
            Integer valueBase = signalModel == null ? 1 : signalModel.getValueBase();
            double value1 = Double.valueOf(value + "");
            coData.put("value", value1 / valueBase);
            coData.put("name", signalModel != null ? signalModel.getName() : null);
            coData.put("unit", signalModel != null ? signalModel.getUnit() : null);
            coData.put("type", signalModel != null ? signalModel.getType() : null);
            JSONObject data = tranceDataId(coId); //数据点翻译
            coData.putAll(data);
            jsonArray.add(coData);
        }
        /*for (String key : mntList) { //通过key获取实时数据
            JSONObject coData = new JSONObject();
            Object value = redisUtils.getHash(RedisHashTable.SN_DATA_HASH + sn, key);
            String coId = key.replaceFirst(dev + "_", "");
            coData.put("coId", coId);
            SignalModel signalModel = configDao.findSignalModelByDeviceTypeAndCoId(type, coId);
            Integer valueBase = signalModel == null ? 1 : signalModel.getValueBase();
            double value1 = Double.valueOf(value + "");
            coData.put("value", value1 / valueBase);
            coData.put("name", signalModel != null ? signalModel.getName() : null);
            coData.put("unit", signalModel != null ? signalModel.getUnit() : null);
            coData.put("type", signalModel != null ? signalModel.getType() : null);
            JSONObject data = tranceDataId(coId); //数据点翻译
            coData.putAll(data);
            jsonArray.add(coData);
        }*/
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
    private JSONObject tranceDataId(String coId) {
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
        return data;

    }

    @Override
    public JSONObject setThreshold(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        String msgId = moduleMsg.getMsgId();
        JSONObject result = new JSONObject();
        JSONObject alarmSignal = moduleMsg.getPayload();
        String deviceId = (String) alarmSignal.get("deviceId");
        String coId = (String) alarmSignal.get("coId");
        String configId = (String) alarmSignal.get("configId");
        String alarmId = (String) alarmSignal.get("alarmId");
        String dev = (String) alarmSignal.get("dev");
        Double threshold = alarmSignal.get("threshold") == null ? null : Double.parseDouble(alarmSignal.get("threshold") + "");
        Float hystersis = alarmSignal.get("hystersis") == null ? null : Float.parseFloat(alarmSignal.get("hystersis") + "");
        Integer relativeval = alarmSignal.get("relativeval") == null ? null : (Integer) alarmSignal.get("relativeval");
        Integer level = alarmSignal.get("level") == null ? null : (Integer) alarmSignal.get("level");
        Integer delay = alarmSignal.get("delay") == null ? null : (Integer) alarmSignal.get("delay");

        if (StringUtils.isBlank(dev)) { //根据deviceId获取dev 门户场景
            logger.info("[{}] sn [{}] set threshold by web/app ", msgId, sn);
            Device device = deviceDao.findDeviceById(deviceId);
            int devType = device.getType();
            int resNo = device.getResNo();
            dev = devType + "-" + resNo; //设备
        }

        if (StringUtils.isBlank(coId)) { //根据告警id和dev获取coId 外部业务场景
            logger.info("[{}] sn [{}]  set threshold by thirdParty ", msgId, sn);
            Device device = deviceDao.findDeviceByTypeResNoPort(sn,
                    Integer.parseInt(dev.split("-")[0]), Integer.parseInt(dev.split("-")[1]), null);
            AlarmSignalConfig alarmSignalConfig = configDao.findAlarmSignalConfigByDeviceIdAndAlarmId(device.getId(), alarmId);
            coId = alarmSignalConfig.getCoId();
            configId = alarmSignalConfig.getId();
        }

        String alarmConfigKey = sn + "_" + dev + "_" + coId;//redis 告警配置键值
        AlarmSignalConfig alarmSignalConfig = configDao.findAlarmSignalConfigById(configId);
        if (alarmSignalConfig != null) {
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
            alarmId = alarmSignalConfig.getAlarmId();

        }
        //设置内存值
        JSONArray redisSignalObj = redisUtils.getHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, alarmConfigKey, JSONArray.class);
        if (redisSignalObj != null) {
            List<AlarmSignalConfig> alarmSignals = JSONArray.parseArray(redisSignalObj.toString(), AlarmSignalConfig.class);
            for (AlarmSignalConfig signalConfig : alarmSignals) {
                String alarmId1 = signalConfig.getAlarmId();
                if (alarmId1.equals(alarmId)) {
                    signalConfig.setThreshold(threshold);
                    break;
                }
            }
            //推送门限值
            Map<String, List<AlarmSignalConfig>> alarmConfigKeyMap = new HashMap<>();
            alarmConfigKeyMap.put(alarmConfigKey, alarmSignals);
            JSONObject jsonObject = alarmConfigPush(msgId, sn, dev, alarmConfigKeyMap); //web/app设置门限值推送至终端
            if (jsonObject != null) {
                int rInt = jsonObject.getInteger("result");
                if (rInt == 0) {
                    logger.error("[{}] sn [{}]  push threshold to terminal error", msgId, sn);
                    return jsonObject;
                }
            }
            //设置数据库值
            configDao.saveAlarmSignalConfig(alarmSignalConfig);
            //设置内存值
            redisUtils.setHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, alarmConfigKey, alarmSignals);
            return jsonObject;
        } else {
            logger.warn("[{}] sn [{}]  set threshold in redis is null ", msgId, sn);
        }
        result.put("result", 1);
        return result;
    }

    /**
     * 推送门限告警
     *
     * @param alarmConfigKeyMap 门限配置
     */
    public JSONObject alarmConfigPush(String msgId, String sn, String devId, Map<String, List<AlarmSignalConfig>> alarmConfigKeyMap) {

        JSONObject result = new JSONObject();
        //根据规则判断是否下发门限值至终端
        if (!needPushAlarmConfig(sn)) {
            result.put("result", 1);
            logger.warn("[{}] sn [{}] needn't push alarmConfig(推送门限值) dev: [{}]", msgId, sn, devId);
            return result;
        }
        logger.info("[{}] sn [{}] push alarmConfig(推送门限值) dev: [{}]", msgId, sn, devId);
        List<JSONObject> payloads = new ArrayList<>(); //分包发送 6个数据点为一个包
        JSONObject payload = new JSONObject();
        payload.put("dev", devId);
        List<Map<String, Object>> pointThresholds = new ArrayList<>();
        payload.put("points", pointThresholds);
        //告警配置(门限)格式化
        for (String alarmConfigKey : alarmConfigKeyMap.keySet()) { //遍历内存告警点key   coId
            //提取dev pointId(coId)
            String[] keyArr = alarmConfigKey.split("_");
            String coId = keyArr[2];
            JSONObject pointThreshold = new JSONObject();

            List<AlarmSignalConfig> alarmSignalConfigs = alarmConfigKeyMap.get(alarmConfigKey);
            if (CollectionUtils.isEmpty(alarmSignalConfigs)) continue;
            boolean addFlag = false;
            for (AlarmSignalConfig alarmSignalConfig : alarmSignalConfigs) {
                Integer coType = alarmSignalConfig.getCoType();
                if (coType == null) {
                    logger.error("[{}] sn [{}] push alarmConfig error coType is null : [{}]", msgId, sn, alarmSignalConfig.toString());
                    continue;
                }
                if (coType.equals(2)) continue;
                addFlag = true;
                Double threshold = alarmSignalConfig.getThreshold();
                Integer thresholdBase = alarmSignalConfig.getThresholdBase();
                Integer thresholdFlag = alarmSignalConfig.getThresholdFlag();
                int value = (int) (threshold * thresholdBase);
                JSONArray thresholdArray = pointThreshold.getJSONArray(thresholdFlag + "");
                if (thresholdArray == null) {
                    thresholdArray = new JSONArray();
                    pointThreshold.put(thresholdFlag + "", thresholdArray);
                }
                thresholdArray.add(Integer.toUnsignedLong(value));

            }
            if (addFlag) {
                if (pointThresholds.size() > 3) { //分包 4 个信号点为一包
                    payloads.add(payload);
                    payload = new JSONObject();
                    payload.put("dev", devId);
                    pointThresholds = new ArrayList<>();
                    payload.put("points", pointThresholds);

                }
                pointThreshold.put("point", Integer.valueOf(coId));
                pointThresholds.add(pointThreshold);
            }
        }
        payloads.add(payload);
        for (JSONObject p : payloads) { //分包发送
            JSONArray points = p.getJSONArray("points");
           /* if (CollectionUtils.isEmpty(points)) {
                result.put("result", 1);
                logger.warn("[{}] sn [{}] needn't push alarmConfig(推送门限值) dev: [{}]", msgId, sn, devId);
                return result;
            }*/
            ModuleMsg moduleMsg = new ModuleMsg(PktType.SET_THRESHOLD_TERMINAL, sn);
            try {
                //向终端推送门限报文
                moduleMsg.setPayload(p);
                Thread.sleep(500L);
                logger.info("[{}] sn [{}]  push threshold [{}] to terminal dev:[{}] data:[{}] ", msgId, sn, PktType.SET_DATA_TERMINAL, devId, p);
                RpcNotifyProto.RpcMessage rpcMessage = rpcModule.postMsg(moduleMsg.getMsgId(),
                        new InetSocketAddress(controllerHost, controllerPort),
                        JSONObject.toJSONString(moduleMsg));
                if (RpcNotifyProto.MessageType.ERROR.equals(rpcMessage.getType())) {
                    String payload1 = rpcMessage.getPayload();
                    return JSONObject.parseObject(payload1);
                }
                result.put("result", 1);

            } catch (IOException e) {
                logger.error("[{}] sn [{}]  push threshold [{}] to terminal error [{}] ", msgId, sn, PktType.SET_DATA_TERMINAL, e.toString());
                //日志记录
                saveLog(moduleMsg.getMsgId(), sn, moduleMsg.getPktType(), StateCode.CONNECT_ERROR);
                result.put("result", 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * @param sn
     * @return 返回false 不下发至终端
     */
    private boolean needPushAlarmConfig(String sn) {
        //设备离线不要下发
        //获取redis 信息
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        JSONObject value = redisUtils.get(key, JSONObject.class);
        if (value != null) {
            int status = value.getInteger("STATUS");
            if (status == 0) return false;
            //获取版本属性
            Terminal terminal = terminalDao.findTerminalBySn(sn);
            TerminalProperties terminalProperties = terminalDao.findTerminalPropertiesByTerminalId(terminal.getId());
            String engineVer = terminalProperties.getEngineVer();
            // 不需要下发的版本
            if (Arrays.asList(this.engineVer.split(",")).contains(engineVer)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


    @Override
    public JSONArray getThreshold(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject payload = moduleMsg.getPayload();
        Integer devType = (Integer) payload.get("type");
        Integer resNo = (Integer) payload.get("resNo");
        String port = (String) payload.get("port");
        String alarmDesc = payload.getString("alarmDesc");
        Device device = deviceDao.findDeviceByTypeResNoPort(sn, devType, resNo, port); //根据信号点的devType获取deviceId
        if (device == null) return new JSONArray();
        String coId = (String) payload.get("coId");
        String alarmId = payload.getString("alarmId");
        List<AlarmSignalConfig> alarmSignalConfigList = configDao.findAlarmSignalConfigByDeviceIdAndCoId(device.getId(), coId, alarmId, alarmDesc); //根据deviceId和数据点id获取信号点配置列表
        return (JSONArray) JSONArray.toJSON(alarmSignalConfigList);
    }

    @Override
    public JSONObject setData(ModuleMsg moduleMsg) {
        String msgId = moduleMsg.getMsgId();
        String sn = moduleMsg.getSN();
        JSONObject payload = moduleMsg.getPayload();
        String dev = (String) payload.get("dev");
        Integer devType = Integer.parseInt(dev.split("-")[0]);
        String coId = payload.getString("stePoint");
        if (StringUtils.isBlank(coId)) payload.getString("setPoint");
        Double data = payload.getDouble("steData");
        if (data == null) payload.getInteger("setData");
        SignalModel signalModel = configDao.findSignalModelByDeviceTypeAndCoId(devType, coId);
        Integer valueBase = signalModel == null ? 1 : signalModel.getValueBase();
        JSONObject terminalPayload = payload;
        int terminalData = new Long(Math.round(data * valueBase)).intValue();
        terminalPayload.put("steData", terminalData);

        try {
            logger.info("[{}] sn [{}]  set data [{}]to terminal [point:{},data:{}] ", msgId, sn, PktType.SET_DATA_TERMINAL, coId, terminalPayload);
            // 向终端设置信号点值报文
            moduleMsg.setPktType(PktType.SET_DATA_TERMINAL);
            moduleMsg.setPayload(terminalPayload);
            RpcNotifyProto.RpcMessage rpcMessage = rpcModule.postMsg(msgId,
                    new InetSocketAddress(controllerHost, controllerPort),
                    JSONObject.toJSONString(moduleMsg));
            if (!RpcNotifyProto.MessageType.ERROR.equals(rpcMessage.getType())) {
                String payload1 = rpcMessage.getPayload();
                return JSONObject.parseObject(payload1);
            }
        } catch (IOException e) {
            logger.error("[{}] sn [{}]  set data [{}]to terminal error [{}] ", msgId, sn, PktType.SET_DATA_TERMINAL, e.toString());
            //日志记录
            saveLog(msgId, sn, moduleMsg.getPktType(), StateCode.CONNECT_ERROR);
        }
        JSONObject result = new JSONObject();
        result.put("result", 0);
        return result;
    }

    @Override
    public JSONObject parseData(ModuleMsg moduleMsg) {
        String msgId = moduleMsg.getMsgId();
        String sn = moduleMsg.getSN();
        logger.info("[{}] sn [{}] report data to thirdParty parse..:{}", msgId, sn, JSONObject.toJSONString(moduleMsg));
        JSONObject terminalPayload = moduleMsg.getPayload();
        int pktType = terminalPayload.getInteger("pktType");
        if (pktType == 4)
            moduleMsg.setPktType(PktType.DATA_CHANGE);
        if (pktType == 5)
            moduleMsg.setPktType(PktType.DATA_REPORT);

        JSONArray data = terminalPayload.getJSONArray("data");
        for (Object devObject : data) {
            JSONObject devJson = (JSONObject) devObject;
            String dev = devJson.getString("dev");
            JSONObject info = devJson.getJSONObject("info");
            Set<String> coIds = info.keySet();
            for (String coId : coIds) {
                int value = info.getInteger(coId);
                SignalModel signalModel = configDao.findSignalModelByDeviceTypeAndCoId(Integer.parseInt(dev.split("-")[0]), coId);
                Integer valueBase = (signalModel == null ? 1 : signalModel.getValueBase());
                info.put(coId, (double) value / (valueBase == 0 ? 1 : valueBase));
            }
        }
        // 向向外部网关发送运行状态报文
        try {
            logger.info("[{}] sn [{}] report data to thirdParty ", msgId, sn);
            rpcModule.postMsg(msgId, new InetSocketAddress(controllerHost, controllerPort), JSONObject.toJSONString(moduleMsg));
        } catch (IOException e) {
            logger.info("[{}] sn [{}] report data to thirdParty error [{}] ", msgId, sn, e.toString());
            //日志记录
            saveLog(msgId, sn, moduleMsg.getPktType(), StateCode.CONNECT_ERROR);
            e.printStackTrace();
        }

        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }


    @Override
    public JSONObject saveRunStatus(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        String msgId = moduleMsg.getMsgId();
        //获取redis 信息
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        JSONObject value = redisUtils.get(key, JSONObject.class);
        JSONObject result = new JSONObject();
        if (value != null && (int) value.get("STATUS") == 2) {
            JSONObject payload = moduleMsg.getPayload();
            RunState runState = new RunState();
            runState.setSn(sn);
            runState.setCpuUse((String) payload.get("cpuUse"));
            runState.setMemUse((String) payload.get("memUse"));
            runState.setSysTime((Long.parseLong(payload.get("sysTime") + "")));
            runState.setCsq((Integer) payload.get("csq"));
            runState.setFlashStatus((Integer) payload.get("flashStatus"));
            runState.setEleCom((Integer) payload.get("eleCom"));
            runState.setCreateTime(new Date());
            runStateDao.saveRunState(runState);
            businessExecutor.execute(() -> {
                // 向向外部网关发送运行状态报文
                try {
                    logger.info("[{}] sn [{}] run status data to thirdParty pktType[{}] ", msgId, sn, PktType.DATA_STATUS);
                    moduleMsg.setPktType(PktType.DATA_STATUS);
                    rpcModule.postMsg(moduleMsg.getMsgId(), new InetSocketAddress(controllerHost, controllerPort), JSONObject.toJSONString(moduleMsg));
                } catch (IOException e) {
                    logger.info("[{}] sn [{}] run status data to thirdParty pktType[{}] error [{}] ", msgId, sn, PktType.DATA_STATUS, e.toString());
                    //日志记录
                    saveLog(msgId, sn, moduleMsg.getPktType(), StateCode.CONNECT_ERROR);
                    e.printStackTrace();
                }
            });
            result.put("result", 1);
            return result;
        }
        //日志记录
        saveLog(msgId, sn, moduleMsg.getPktType(), value != null ? StateCode.UNREGISTY : StateCode.CONNECT_ERROR);
        result.put("result", StateCode.UNREGISTY);
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


    /**
     * 保存运行时错误日志
     *
     * @param msgId
     * @param sn
     * @param msgType
     * @param stateCode
     */
    void saveLog(String msgId, String sn, String msgType, int stateCode) {
        Log log = new Log(new Date(System.currentTimeMillis()),
                stateCode,
                sn, msgType, msgId, name, host);
        logService.saveLog(log);
    }


}
