package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.dao.ConfigDao;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import com.kongtrolink.framework.execute.module.dao.TerminalDao;
import com.kongtrolink.framework.execute.module.model.*;
import com.kongtrolink.framework.execute.module.model.Device;
import com.kongtrolink.framework.execute.module.service.RegistryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mystoxlol on 2019/3/27, 23:48.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class RegistryServiceImpl implements RegistryService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${register.delay:300}")
    private int delay;
    @Value("${server.bindIp}")
    private String host;
    @Value("${server.name}")
    private String name;
    @Autowired
    TerminalDao terminalDao;
    @Autowired
    LogDao logDao;
    @Autowired
    ConfigDao configDao;
    @Autowired
    DeviceDao deviceDao;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    RpcModule rpcModule;

    @Value("${rpc.controller.hostname}")
    private String controllerHost;
    @Value("${rpc.controller.port}")
    private int controllerPort;


    @Override
    public JSONObject registerSN(ModuleMsg moduleMsg) {

        String sn = moduleMsg.getSN();
        //查表t_terminal
        Terminal terminal = terminalDao.findTerminalBySn(sn);
        JSONObject result = new JSONObject();
        if (terminal != null && otherLogic()) {
            String bid = terminal.getBID();
            Order order = terminalDao.findOrderByBid(bid);
            String bip = order.getBIP();
            //获取redis 信息
            String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
            JSONObject value = redisUtils.get(key, JSONObject.class);
            //删除所有其他信息
            if (value != null) {
                redisUtils.deleteHash(RedisHashTable.SN_DEVICE_LIST_HASH, sn);
                redisUtils.deleteHash(RedisHashTable.SN_DATA_HASH, sn);
//                redisUtils
//                redisUtils.deleteHash(RedisHashTable.SN_DEV_ID_ALARMSIGNAL_HASH, sn);

                value.put("BIP", bip);
                value.put("STATUS", 1);
                redisUtils.set(key, value);

                result.put("result", 1);
                result.put("time", System.currentTimeMillis() / 1000);
                return result;
            } else {
                logger.error("不存在通讯key[{}]的值", key);
            }
        } else if (!otherLogic()) {
            result.put("result", 0);
            result.put("delay", delay);
        }
        //注册非法默认

        result.put("result", 0);
        result.put("delay", delay);

        //日志记录
        Log log = new Log();
        log.setMsgType(moduleMsg.getPktType());
        log.setErrorCode(1);
        log.setSN(sn);
        log.setMsgId(moduleMsg.getMsgId());
        log.setHostName(host);
        log.setServiceName(name);
        log.setTime(new Date());
        logDao.saveLog(log);
        return result;
    }

    @Override
    public JSONObject registerDevices(ModuleMsg moduleMsg) {
        String sn = moduleMsg.getSN();
        JSONObject devPayload = moduleMsg.getPayload();//设备信息包的报文
        JSONObject result = new JSONObject();
        //获取redis 信息
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        JSONObject value = redisUtils.get(key, JSONObject.class);
        if (value != null && (int) value.get("STATUS") == 1) {
            JSONArray devsJson = (JSONArray) devPayload.get("devList");
            if (devsJson == null) {
                result.put("result", 0);
            }
            List<String> devList = JSONArray.parseArray(devsJson.toJSONString(), String.class);
            result.put("result", 1);
            // 同步告警点信息表至redis

            List<Device> devices = deviceDao.findDevicesBySnAndValid(sn); //获取有效
            List<Device> newDeviceList = new ArrayList<>(); //新增设备
            List<Device> saveDeviceList = new ArrayList<>();
            for (String dev : devList) { //解析格式化设备包
                //devId的组成:类型-端口号-地址-序号-协议编码
                if (StringUtils.isNotBlank(dev) && dev.contains("-")) {
                    String[] devArr = dev.split("-");
                    Integer devType = Integer.parseInt(devArr[0]);
                    Integer devPort = Integer.parseInt(devArr[1]);
                    Integer devResNo = Integer.parseInt(devArr[3]);
                    Device device = new Device();
                    device.setPort(devPort);
                    device.setResNo(devResNo);
                    device.setType(devType);
                    newDeviceList.add(device);

                }
            }
            for (Device device : devices) {//数据库设备表
                Integer deviceType = device.getType();
                Integer devicePort = device.getPort();
                Integer deviceResNo = device.getResNo();
                device.setInvalidTime(new Date(System.currentTimeMillis())); //设置失效
                for (Device newDevice : newDeviceList) {
                    if (deviceType == newDevice.getType()
                            && devicePort == newDevice.getPort()
                            && deviceResNo == newDevice.getResNo()) {
                        device.setInvalidTime(new Date(0L));
                        newDevice.setInvalidTime(new Date(0L));
                        break;
                    }
                }
            }
            for (Device device : newDeviceList) {
                if (device.getInvalidTime() == null) {
                    device.setSN(sn);
                    device.setInvalidTime(new Date(0L));
                    saveDeviceList.add(device);
                }
            }
            deviceDao.saveBatch(saveDeviceList); //保存最新设备信息


            List<Device> deviceList = deviceDao.findDevicesBySnAndValid(sn); //获取最新设备信息

            for (Device device : deviceList) { //根据设备生产最新配置信息表

                String deviceId = device.getId();
                Integer type = device.getType();
                Integer resNo = device.getResNo();
                String devDataId = type + "-" + resNo; //数据点的devId为类型-序号

                List<AlarmSignalConfig> alarmSignals = configDao.findAlarmSignalConfigByDevId(deviceId);

                if (alarmSignals == null ||alarmSignals.size() < 1) { //根据模版表赋值
                    alarmSignals = new ArrayList<>();
                    List<AlarmSignalConfigModel> alarmSignalModels = configDao.findAlarmSignalModelByDevType(type);
                    for (AlarmSignalConfigModel alarmSignalConfigModel : alarmSignalModels)
                    {
                        AlarmSignalConfig alarmSignalConfig = new AlarmSignalConfig();
                        BeanUtils.copyProperties(alarmSignalConfigModel,alarmSignalConfig);
                        alarmSignalConfig.setId(null);
                        alarmSignalConfig.setDeviceId(deviceId);
                        alarmSignals.add(alarmSignalConfig);
                    }
                    configDao.saveAlarmSignalConfig(alarmSignals);
                    alarmSignals = configDao.findAlarmSignalConfigByDevId(deviceId);
                }

                Map<String, List<AlarmSignalConfig>> alarmConfigKeyMap  = new HashMap<>();
                if (alarmSignals != null && alarmSignals.size() > 0) { //根据模版表格式化数据
                    for (AlarmSignalConfig alarmSignalConfig : alarmSignals) {
                        String coId = alarmSignalConfig.getCoId();
                        String alarmConfigKey = sn + "_" + devDataId + "_" + coId;
                        List<AlarmSignalConfig> alarmSignalConfigs = alarmConfigKeyMap.get(alarmConfigKey);
                        if (alarmSignalConfigs !=null) {
                            alarmSignalConfigs.add(alarmSignalConfig);
                        }
                        else {
                            alarmSignalConfigs = new ArrayList<>();
                            alarmSignalConfigs.add(alarmSignalConfig);

                        }
                        alarmConfigKeyMap.put(alarmConfigKey, alarmSignalConfigs);
                    }
                }


                for (String alarmConfigKey : alarmConfigKeyMap.keySet()) {//告警配置写入redis
                        redisUtils.setHash(RedisHashTable.SN_DEV_ID_ALARMSIGNAL_HASH, alarmConfigKey, alarmConfigKeyMap.get(alarmConfigKey));
                }
            }
            redisUtils.setHash(RedisHashTable.SN_DEVICE_LIST_HASH, sn, devList); //设备信息写入redis

            try {
                // 向网关发送业注册报文{"SN","00000",DEVICE_LIST} 即向业务平台事务处理发送注册信息
                moduleMsg.setPktType(PktType.REGISTRY_CNTB);
                rpcModule.postMsg(moduleMsg.getMsgId(), new InetSocketAddress(controllerHost, controllerPort), JSONObject.toJSONString(moduleMsg));
            } catch (IOException e) {
                logger.error("发送至外部业务注册异常" + e.toString());
                //日志记录
                Log log = new Log();
                log.setErrorCode(3);
                log.setSN(sn);
                log.setMsgType(moduleMsg.getPktType());
                log.setMsgId(moduleMsg.getMsgId());
                log.setHostName(host);
                log.setServiceName(name);
                log.setTime(new Date(System.currentTimeMillis()));
                logDao.saveLog(log);
            }
            return result;
        }


        //日志记录
        Log log = new Log();
        log.setErrorCode(3);
        log.setSN(sn);
        log.setMsgType(moduleMsg.getPktType());
        log.setMsgId(moduleMsg.getMsgId());
        log.setHostName(host);
        log.setServiceName(name);
        log.setTime(new Date(System.currentTimeMillis()));
        logDao.saveLog(log);
        result.put("result", 0);
        return result;
    }


    @Override
    public JSONObject registerTerminal(ModuleMsg moduleMsg) {

        JSONObject payload = moduleMsg.getPayload();
        String sn = moduleMsg.getSN();
        Terminal terminal = terminalDao.findTerminalBySn(sn);
        String terminalId = terminal.getId();
        TerminalProperties terminalProperties = terminalDao.findTerminalPropertiesByTerminalId(terminalId);
        if (terminalProperties == null) {
            terminalProperties = new TerminalProperties();
            terminalProperties.setTerminalId(terminalId);
        }
        terminalProperties.setAccessMode((Integer) payload.get("accessMode"));
        terminalProperties.setCarrier((String) payload.get("carrier"));
        terminalProperties.setNwType((String) payload.get("nwType"));
        terminalProperties.setWmType((String) payload.get("wmType"));
        terminalProperties.setWmVendor((String) payload.get("wmVendor"));
        terminalProperties.setImei((String) payload.get("imsi"));
        terminalProperties.setImei((String) payload.get("imei"));
        terminalProperties.setEngineVer((String) payload.get("engineVer"));
        terminalProperties.setSignalStrength((Integer) payload.get("signalStrength"));
        terminalProperties.setAdapterVer((String) payload.get("adapterVer"));
        terminalDao.saveTerminal(terminalProperties);
        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }

    private boolean otherLogic() {
        return true;
    }

    @Override
    public JSONObject saveCleanupLog(ModuleMsg moduleMsg) {
        JSONObject msgPayload = moduleMsg.getPayload();
        String uuid = moduleMsg.getUuid();
        String sn = (String) msgPayload.get("SN");
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        redisUtils.del(key);
        redisUtils.deleteHash(RedisHashTable.SN_DEVICE_LIST_HASH, sn);
        redisUtils.deleteHash(RedisHashTable.SN_DATA_HASH, sn);

        //日志记录
        Log log = new Log();
        log.setMsgType(moduleMsg.getPktType());
        log.setMsgId(moduleMsg.getMsgId());
        log.setErrorCode((Integer) msgPayload.get("code"));
        log.setHostName((String) msgPayload.get("serverHost"));
        log.setServiceName((String) msgPayload.get("serverName"));
        log.setTime(new Date((Long) msgPayload.get("time")));
        logDao.saveLog(log);

        JSONObject result = new JSONObject();
        result.put("result", 1);
        return result;
    }

}
