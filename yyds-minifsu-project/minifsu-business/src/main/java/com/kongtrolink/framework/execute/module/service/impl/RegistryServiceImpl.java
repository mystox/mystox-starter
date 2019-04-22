package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSON;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;


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
    @Value("${server.rpc.port}")
    private String port;
    @Value("${server.name}")
    private String name;
    private final TerminalDao terminalDao;
    private final LogDao logDao;
    private final ConfigDao configDao;
    private final DeviceDao deviceDao;
    private final RedisUtils redisUtils;
    private final RpcModule rpcModule;
    private final ThreadPoolTaskExecutor businessExecutor;
    @Value("${rpc.controller.hostname}")
    private String controllerHost;
    @Value("${rpc.controller.port}")
    private int controllerPort;

    @Autowired
    public RegistryServiceImpl(TerminalDao terminalDao, LogDao logDao, ConfigDao configDao, DeviceDao deviceDao, RedisUtils redisUtils, RpcModule rpcModule, ThreadPoolTaskExecutor businessExecutor) {
        this.terminalDao = terminalDao;
        this.logDao = logDao;
        this.configDao = configDao;
        this.deviceDao = deviceDao;
        this.redisUtils = redisUtils;
        this.businessExecutor = businessExecutor;
        this.rpcModule = rpcModule;
    }


    @Override
    public JSONObject registerSN(ModuleMsg moduleMsg) {

        String sn = moduleMsg.getSN();
        //查表t_terminal
        Terminal terminal = terminalDao.findTerminalBySn(sn);
        JSONObject result = new JSONObject();
        if (terminal != null && otherLogic()) {
            String bid = terminal.getBID();
            Order order = terminalDao.findOrderById(bid);

            //获取redis 信息
            String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
            JSONObject value = redisUtils.get(key, JSONObject.class);
            //删除所有其他信息
            if (value != null) {
                redisUtils.deleteHash(RedisHashTable.SN_DEVICE_LIST_HASH, sn);
                redisUtils.del(RedisHashTable.SN_DATA_HASH + sn);
                Set<String> keys = redisUtils.getHkeys(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, sn + "*");
                if (keys != null && keys.size() > 0) {
                    String[] s = new String[keys.size()];
                    redisUtils.deleteHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, keys.toArray(s));
                }
                if (order != null) {
                    String bip = order.getBIP(); //业务网关的消息路由
                    value.put("BIP", bip);
                }
                value.put("STATUS", 1);
                //心跳节拍值设置
                Integer heartCycle = terminal.getHeartCycle();
                if (heartCycle == null || heartCycle == 0)
                    heartCycle = 10;//默认10s
                value.put("heartCycle", heartCycle);
                Integer businessRhythm = terminal.getBusinessRhythm();
                if (businessRhythm == null || businessRhythm == 0)
                    businessRhythm = 30;//默认10s
                value.put("businessRhythm", businessRhythm);
                Integer alarmRhythm = terminal.getAlarmRhythm();
                if (alarmRhythm == null || alarmRhythm == 0)
                    alarmRhythm = 1;//默认10s
                value.put("alarmRhythm", alarmRhythm);
                Integer runStatusRhythm = terminal.getRunStatusRhythm();
                if (runStatusRhythm == null || runStatusRhythm == 0)
                    runStatusRhythm = 100;//默认10s
                value.put("runStatusRhythm", runStatusRhythm);

                int miniCount = Math.min(Math.min(businessRhythm, alarmRhythm), runStatusRhythm);
                int expiredTime = miniCount * heartCycle * 3;
                value.put("expired", expiredTime); //三倍最低心跳时间
                redisUtils.set(key, value);
                redisUtils.expired(key, expiredTime, TimeUnit.SECONDS);
                result.put("result", 1);
                result.put("time", System.currentTimeMillis() / 1000);
                result.put("heartCycle", heartCycle);
                result.put("businessRhythm", businessRhythm);
                result.put("alarmRhythm", alarmRhythm);
                result.put("runStatusRhythm", runStatusRhythm);
                return result;
            } else {
                logger.error("不存在通讯key[{}]的值", key);
            }
        } else if (!otherLogic()) {
            result.put("result", StateCode.UNREGISTY);
            result.put("delay", delay + (long) (Math.random() * delay));// delay 随机值
        }
        //注册非法默认

        result.put("result", StateCode.UNREGISTY);
        result.put("delay", (long) (Math.random() * delay));
        //日志记录
        Log log = new Log();
        log.setMsgType(moduleMsg.getPktType());
        log.setErrorCode(StateCode.ILLEGAL_LOG);
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
        if (value != null && (int) value.get("STATUS") != 0) {
            Object devsObject = devPayload.get("devList");
            if (devsObject == null) {
                result.put("result", StateCode.UNREGISTY);
                logger.error("devList is null");
                return result;
            }
            JSONArray devsJson = (JSONArray) devsObject;
            List<String> devList = JSONArray.parseArray(devsJson.toJSONString(), String.class);

            // 同步告警点信息表至redis

            List<Device> devices = deviceDao.findDevicesBySnAndValid(sn); //获取有效
            List<Device> newDeviceList = new ArrayList<>(); //新增设备
            List<Device> saveDeviceList = new ArrayList<>();
            for (String dev : devList) { //解析格式化设备包
                //devId的组成:类型-端口号-地址-序号-协议编码
                if (StringUtils.isNotBlank(dev) && dev.contains("-")) {
                    String[] devArr = dev.split("-");
                    Integer devType = Integer.parseInt(devArr[0]);
                    String devPort = devArr[1];
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
                String devicePort = device.getPort();
                Integer deviceResNo = device.getResNo();
                device.setInvalidTime(new Date(System.currentTimeMillis())); //设置失效
                for (Device newDevice : newDeviceList) {
                    if (deviceType.equals(newDevice.getType())
                            && devicePort.equals(newDevice.getPort())
                            && deviceResNo.equals(newDevice.getResNo())) { //存在对应类型设置有效
                        device.setInvalidTime(new Date(0L));
                        newDevice.setInvalidTime(new Date(0L));
                        break;
                    }
                }
                deviceDao.save(device);
            }
            for (Device device : newDeviceList) { //未设置的新增设备初始化存入数据库表
                if (device.getInvalidTime() == null) {
                    device.setSN(sn);
                    device.setInvalidTime(new Date(0L));
                    saveDeviceList.add(device);
                }
            }
            deviceDao.saveBatch(saveDeviceList); //保存最新设备信息

            List<Device> deviceList = deviceDao.findDevicesBySnAndValid(sn); //获取最新设备信息
            redisUtils.setHash(RedisHashTable.SN_DEVICE_LIST_HASH, sn, devList); //设备信息写入redis
            for (Device device : deviceList) { //根据设备产生最新配置信息表

                String deviceId = device.getId();
                Integer type = device.getType();
                Integer resNo = device.getResNo();
                String devDataId = type + "-" + resNo; //数据点的devId为类型-序号

                List<AlarmSignalConfig> alarmSignals = configDao.findAlarmSignalConfigByDevId(deviceId);

                if (alarmSignals == null || alarmSignals.size() < 1) { //根据模版表赋值
                    alarmSignals = new ArrayList<>();
                    List<AlarmSignalConfigModel> alarmSignalModels = configDao.findAlarmSignalModelByDevType(type);
                    for (AlarmSignalConfigModel alarmSignalConfigModel : alarmSignalModels) {
                        AlarmSignalConfig alarmSignalConfig = new AlarmSignalConfig();
                        BeanUtils.copyProperties(alarmSignalConfigModel, alarmSignalConfig);
                        alarmSignalConfig.setId(null);
                        alarmSignalConfig.setDeviceId(deviceId);
                        alarmSignals.add(alarmSignalConfig);
                    }
                    configDao.saveAlarmSignalConfig(alarmSignals);
                    alarmSignals = configDao.findAlarmSignalConfigByDevId(deviceId);
                }

                Map<String, List<AlarmSignalConfig>> alarmConfigKeyMap = new HashMap<>();
                if (alarmSignals != null && alarmSignals.size() > 0) { //根据模版表格式化数据
                    for (AlarmSignalConfig alarmSignalConfig : alarmSignals) {
                        String coId = alarmSignalConfig.getCoId();

                        String alarmConfigKey = sn + "_" + devDataId + "_" + coId;
                        List<AlarmSignalConfig> alarmSignalConfigs = alarmConfigKeyMap.get(alarmConfigKey);
                        //根据告警点属性的valueBase处理数据点值的倍数问题!!
                        SignalModel signalModel = configDao.findSignalModelByDeviceTypeAndCoId(type, coId);
                        alarmSignalConfig.setThresholdBase(signalModel == null ? 1 :
                                signalModel.getValueBase());

                        if (alarmSignalConfigs != null) {
                            alarmSignalConfigs.add(alarmSignalConfig);
                        } else {
                            alarmSignalConfigs = new ArrayList<>();
                            alarmSignalConfigs.add(alarmSignalConfig);

                        }
                        alarmConfigKeyMap.put(alarmConfigKey, alarmSignalConfigs);
                    }
                }


                for (String alarmConfigKey : alarmConfigKeyMap.keySet()) {//告警配置写入redis
                    redisUtils.setHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, alarmConfigKey, JSON.toJSON(alarmConfigKeyMap.get(alarmConfigKey)));
                }
            }

            value.put("STATUS", 2);
            int expiredTime = (int) value.get("expired");
            redisUtils.set(key, value, expiredTime);
            businessExecutor.execute(() -> {
                try {
                    // 向网关发送业注册报文{"SN","00000",DEVICE_LIST} 即向业务平台事务处理发送注册信息
                    moduleMsg.setPktType(PktType.REGISTRY_CNTB);
                    JSONObject registerJson = (JSONObject) JSONObject.toJSON(moduleMsg);
                    registerJson.put("innerIp", host);
                    registerJson.put("innerPort", port);
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
            });


            //设备上报流程执行完毕
            result.put("result", StateCode.SUCCESS);
            return result;
        }


        //日志记录
        Log log = new Log();
        if (value != null) log.setErrorCode(StateCode.UNREGISTY); //判断通讯异常
        else log.setErrorCode(StateCode.CONNECT_ERROR);
        log.setSN(sn);
        log.setMsgType(moduleMsg.getPktType());
        log.setMsgId(moduleMsg.getMsgId());
        log.setHostName(host);
        log.setServiceName(name);
        log.setTime(new Date(System.currentTimeMillis()));
        logDao.saveLog(log);
        result.put("result", StateCode.UNREGISTY);
        return result;
    }


    @Override
    public JSONObject registerTerminal(ModuleMsg moduleMsg) {

        JSONObject payload = moduleMsg.getPayload();
        String sn = moduleMsg.getSN();
        JSONObject result = new JSONObject();
        //获取redis 信息
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        JSONObject value = redisUtils.get(key, JSONObject.class);
        if (value != null && (int) value.get("STATUS") != 0) {
            Terminal terminal = terminalDao.findTerminalBySn(sn);
            if (terminal == null) {
                result.put("result", StateCode.UNREGISTY);
                return result;
            }
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
            terminalProperties.setImsi((String) payload.get("imsi"));
            terminalProperties.setImei((String) payload.get("imei"));
            terminalProperties.setEngineVer((String) payload.get("engineVer"));
            terminalProperties.setSignalStrength((Integer) payload.get("signalStrength"));
            terminalProperties.setAdapterVer((String) payload.get("adapterVer"));

            try {
                terminalDao.saveTerminalProperties(terminalProperties);
                result.put("result", StateCode.SUCCESS);
                return result;
            } catch (Exception e) {
                result.put("result", StateCode.UNREGISTY);
                e.printStackTrace();
            }
        }
        //日志记录
        Log log = new Log();
        if (value != null) log.setErrorCode(StateCode.UNREGISTY); //判断通讯异常
        else log.setErrorCode(StateCode.CONNECT_ERROR);
        log.setSN(sn);
        log.setMsgType(moduleMsg.getPktType());
        log.setMsgId(moduleMsg.getMsgId());
        log.setHostName(host);
        log.setServiceName(name);
        log.setTime(new Date(System.currentTimeMillis()));
        logDao.saveLog(log);
        result.put("result", StateCode.UNREGISTY);
        return result;
    }

    private boolean otherLogic() {
        return true;
    }

    @Override
    public JSONObject saveCleanupLog(ModuleMsg moduleMsg) {
        JSONObject msgPayload = moduleMsg.getPayload();
        String uuid = moduleMsg.getUuid();

        //redis 通过uuid反查sn
        if (StringUtils.isNotBlank(uuid)) {

            String pattern = RedisHashTable.COMMUNICATION_HASH + "*";
            Set<String> keySet = redisUtils.keys(pattern);
            for (String key : keySet) {
                JSONObject value = redisUtils.get(key, JSONObject.class);
                if (uuid.equals(value.get("UUID"))) {
                    String sn = key.replace(RedisHashTable.COMMUNICATION_HASH + ":", "");
                    redisUtils.del(key);
                    redisUtils.deleteHash(RedisHashTable.SN_DEVICE_LIST_HASH, sn);
                    redisUtils.del(RedisHashTable.SN_DATA_HASH + sn);
                    Set<String> keys = redisUtils.getHkeys(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, sn + "*");
                    if (keys != null && keys.size() > 0) {
                        String[] s = new String[keys.size()];
                        redisUtils.deleteHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, keys.toArray(s));
                    }
                    //日志记录
                    Log log = new Log();
                    log.setSN(sn);
                    log.setMsgType(moduleMsg.getPktType());
                    log.setMsgId(moduleMsg.getMsgId());
                    log.setErrorCode((Integer) msgPayload.get("code"));
                    log.setHostName((String) msgPayload.get("serverHost"));
                    log.setServiceName((String) msgPayload.get("serverName"));
                    log.setTime(new Date((Long.valueOf(msgPayload.get("time") + ""))));
                    logDao.saveLog(log);
                }
            }
        }


        JSONObject result = new JSONObject();
        result.put("result", StateCode.SUCCESS);
        return result;
    }

    @Override
    public JSONObject terminalHeart(ModuleMsg moduleMsg) {
        JSONObject payload = moduleMsg.getPayload();
        String sn = moduleMsg.getSN();
        JSONObject result = new JSONObject();
        //获取redis 信息
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        JSONObject value = redisUtils.get(key, JSONObject.class);
        if (value != null && (int) value.get("STATUS") == 2) {
            result.put("result", StateCode.SUCCESS);
            return result;
        }
        //日志记录
        Log log = new Log();
        if (value != null) log.setErrorCode(StateCode.UNREGISTY); //判断通讯异常
        else log.setErrorCode(StateCode.CONNECT_ERROR);
        log.setSN(sn);
        log.setMsgType(moduleMsg.getPktType());
        log.setMsgId(moduleMsg.getMsgId());
        log.setHostName(host);
        log.setServiceName(name);
        log.setTime(new Date(System.currentTimeMillis()));
        logDao.saveLog(log);
        result.put("result", StateCode.UNREGISTY);
        return result;
    }

}

