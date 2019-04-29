package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
import com.kongtrolink.framework.entity.RedisTable;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.xml.base.MessageResp;
import com.kongtrolink.framework.entity.xml.msg.GetData;
import com.kongtrolink.framework.entity.xml.msg.GetFsuInfo;
import com.kongtrolink.framework.entity.xml.msg.*;
import com.kongtrolink.framework.entity.xml.util.MessageUtil;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.dao.*;
import com.kongtrolink.framework.execute.module.model.*;
import com.kongtrolink.framework.jsonType.JsonRegistry;
import com.kongtrolink.framework.jsonType.JsonStation;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonLoginParam;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fengw
 * 处理铁塔业务逻辑
 * 新建文件 2019-4-15 17:04:42
 */
@Service
public class TowerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    StationDao stationDao;
    @Autowired
    VpnDao vpnDao;
    @Autowired
    LoginParamDao loginParamDao;
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    DevTypeDao devTypeDao;
    @Autowired
    CarrierDao carrierDao;
    @Autowired
    SignalDao signalDao;
    @Autowired
    AlarmDao alarmDao;
    @Autowired
    AlarmLogDao alarmLogDao;
    @Autowired
    DeviceMatchService deviceMatchService;
    @Autowired
    private RpcModule rpcModule;
    @Autowired
    RpcClient rpcClient;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Value("${tower.gateway.hostname}")
    private String towerGatewayHostname;
    @Value("${tower.gateway.port}")
    private int towerGatewayPort;

    //信号字典表模式
    @Value("${tower.binding.dictMode}")
    private int dictMode;
    //是否解除绑定
    @Value("${tower.binding.disabled}")
    private boolean disabled;
    //解除绑定时间
    @Value("${tower.binding.disabledTime}")
    private long disabledTime;

    //铁塔平台心跳周期(秒)
    @Value("${tower.loginParam.heartbeatInterval}")
    private int heartbeatInterval;
    //心跳超时次数上限
    @Value("${tower.loginParam.heartbeatTimeoutLimit}")
    private int heartbeatTimeoutLimit;
    //注册次数上限
    @Value("${tower.loginParam.loginLimit}")
    private int loginLimit;
    //注册间隔(秒)
    @Value("${tower.loginParam.loginInterval}")
    private int loginInterval;
    //单条告警上报次数上限
    @Value("${tower.loginParam.alarmReportLimit}")
    private int alarmReportLimit;
    //告警上报间隔(秒)
    @Value("${tower.loginParam.alarmReportInterval}")
    private int alarmReportInterval;

    //终端在线超时时间(秒)
    @Value("${tower.registry.timeout}")
    private int registryTimeout;

    /**
     * 绑定业务
     * @param sn sn
     * @param request 绑定信息
     * @return 绑定结果
     */
    public boolean fsuBind(String sn, JSONObject request) throws Exception {
        boolean result = false;

        JsonStation jsonStation = getStationInfo(request);

        //更新数据库中的注册参数
        JsonLoginParam jsonLoginParam = getLoginParam(request);

        //更新数据库中的设备列表
        List<JsonDevice> jsonDeviceList = getDeviceList(request, jsonStation.getFsuId());

        JsonStation curStation = stationDao.getInfoByFsuId(jsonStation.getFsuId());
        if (curStation != null) {
            if (!fsuUnbind(curStation.getSn(), null)) {
                logger.error("TERMINAL_UNBIND: 原终端解绑失败,sn:" + curStation.getSn() + ",fsuId:" + curStation.getFsuId());
                return result;
            }
        }

        updateBindRedis(jsonStation, jsonLoginParam, jsonDeviceList);

        //更新基站信息
        result = updateStationBindDB(jsonStation, jsonLoginParam, jsonDeviceList);

        return result;
    }

    /**
     * 获取基站信息
     * @param request 请求信息
     * @return 基站信息
     */
    private JsonStation getStationInfo(JSONObject request) {
        JsonStation info = JSONObject.parseObject(request.toJSONString(), JsonStation.class);
        if (!request.containsKey("dictMode")) {
            info.setDictMode(dictMode);
        }
        if (!request.containsKey("disabled")) {
            info.setDisabled(disabled);
        }
        if (!request.containsKey("disabledTime")) {
            info.setDisabledTime(disabledTime);
        }
        return info;
    }

    /**
     * 获取FSU的注册参数信息
     * @param request 请求信息
     */
    private JsonLoginParam getLoginParam(JSONObject request) {
        JsonLoginParam loginParam = JSONObject.parseObject(request.toJSONString(), JsonLoginParam.class);
        if (!request.containsKey("heartbeatInterval")) {
            loginParam.setHeartbeatInterval(this.heartbeatInterval);
        }
        if (!request.containsKey("heartbeatTimeoutLimit")) {
            loginParam.setHeartbeatTimeoutLimit(this.heartbeatTimeoutLimit);
        }
        if (!request.containsKey("loginLimit")) {
            loginParam.setLoginLimit(this.loginLimit);
        }
        if (!request.containsKey("loginInterval")) {
            loginParam.setLoginInterval(this.loginInterval);
        }
        if (!request.containsKey("alarmReportLimit")) {
            loginParam.setAlarmReportLimit(this.alarmReportLimit);
        }
        if (!request.containsKey("alarmReportInterval")) {
            loginParam.setAlarmReportInterval(this.alarmReportInterval);
        }
        return loginParam;
    }

    /**
     * 更新数据库中的设备列表
     * @param request 请求信息
     * @param fsuId fsuId
     */
    private List<JsonDevice> getDeviceList(JSONObject request, String fsuId) throws Exception {
        List<JsonDevice> result = new ArrayList<>();

        if (fsuId.length() != 14) {
            throw new Exception("铁塔FSUID有误,fsuId:" + fsuId);
        }

        JSONArray array = request.getJSONArray("devCodeList");
        for (int i = 0; i < array.size(); ++i) {
            String deviceId = array.getString(i);
            if (deviceId.equals("")) {
                continue;
            }
            if (deviceId.length() != 14) {
                throw new Exception("铁塔设备ID有误,fsuId:" + fsuId + ",deviceId:" + deviceId);
            }
            result.add(new JsonDevice(fsuId, deviceId));
        }

        List<JsonDevice> curList = new ArrayList<>();
        JSONArray curArray = request.getJSONArray("devList");
        if (curArray != null) {
            for (int i = 0; i < curArray.size(); ++i) {
                curList.add(new JsonDevice(curArray.getString(i)));
            }
        }
        deviceMatchService.matchingDevice(result, curList);

        return result;
    }

    /**
     * 更新数据库中的绑定信息
     * @param jsonStation 基站信息
     * @param jsonLoginParam 注册参数信息
     * @param jsonDeviceList 设备列表
     * @return 更新结果
     */
    private boolean updateStationBindDB(JsonStation jsonStation,
                                        JsonLoginParam jsonLoginParam,
                                        List<JsonDevice> jsonDeviceList) {

        loginParamDao.upsertInfoByFsuId(jsonLoginParam);

        deviceDao.deleteListByFsuId(jsonStation.getFsuId());
        deviceDao.insertListByFsuId(jsonDeviceList);

        JsonStation curStation = stationDao.getInfoByFsuId(jsonStation.getFsuId());
        if (curStation != null) {
            //该FSU在数据库中存在绑定关系
            if (curStation.getSn().equals(jsonStation.getSn())) {
                //若数据库存在记录且SN相同，则更新数据库中该条记录对应的信息
                return stationDao.updateInfoByFsuIdAndSn(jsonStation);
            } else {
                //若数据库存在记录且SN不同，则将该条绑定状态置为解绑，并记录解绑时间
                boolean unbindResult = stationDao.unbindByFsuIdAndSn(curStation.getSn(), curStation.getFsuId());
                redisUtils.del(RedisTable.getRegistryKey(curStation.getSn()));
                if (!unbindResult) {
                    logger.error("TERMINAL_UNBIND: 终端解绑失败,sn:" + curStation.getSn() + ",fsuId:" + curStation.getFsuId());
                }
            }
        }

        stationDao.insertInfo(jsonStation);
        return true;
    }

    /**
     * 更新redis中的绑定信息
     * @param jsonStation 基站信息
     * @param jsonLoginParam 注册参数信息
     * @param jsonDeviceList 设备列表
     */
    private void updateBindRedis(JsonStation jsonStation,
                                 JsonLoginParam jsonLoginParam,
                                 List<JsonDevice> jsonDeviceList) {
        String key = RedisTable.getRegistryKey(jsonStation.getSn());
        if (redisUtils.hasKey(key)) {
            RedisOnlineInfo onlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
            onlineInfo.setStation(jsonStation);
            onlineInfo.setLoginParam(jsonLoginParam);
            onlineInfo.setOnline(false);

            Vpn vpn = vpnDao.getInfoByName(jsonStation.getVpnName());
            if (vpn != null) {
                onlineInfo.setVpn(vpn);
            } else {
                onlineInfo.setLocalName("");
                onlineInfo.setLoginIp("");
                onlineInfo.setLoginPort(0);
            }

            long time = redisUtils.getExpire(key);
            redisUtils.set(key, onlineInfo, time);
        }

        RedisFsuBind redisFsuBind = new RedisFsuBind();
        redisFsuBind.setSn(jsonStation.getSn());
        redisFsuBind.setFsuId(jsonStation.getFsuId());
        for (int i = 0; i < jsonDeviceList.size(); ++i) {
            redisFsuBind.getDeviceIdList().add(jsonDeviceList.get(i).getDeviceId());
        }
        //更新redis中fsuId和sn的映射关系
        redisUtils.set(RedisTable.getFsuBindKey(jsonStation.getFsuId()), redisFsuBind);
    }

    /**
     * 平台注册终端信息
     * @param sn sn
     * @param request 请求信息
     * @return
     */
    public boolean login(String sn, JSONObject request) {
        boolean result = false;

        JsonRegistry jsonRegistry = getRegistryInfo(request);
        String key = RedisTable.getRegistryKey(sn);

        if (!redisUtils.hasKey(key)) {
            //若redis中不存在sn记录，则在数据库中查找
            JsonStation jsonStation = stationDao.getInfoBySn(jsonRegistry.getSn());
            if (jsonStation == null) {
                //若数据库中不存在sn记录，则说明该sn未绑定fsuId，直接向内部服务返回0
                return false;
            }
            //若数据库中存在记录，读取相关信息，写入redis中
            JsonLoginParam jsonLoginParam = loginParamDao.getInfoByFsuId(jsonStation.getFsuId());
            Vpn vpn = vpnDao.getInfoByName(jsonStation.getVpnName());

            RedisOnlineInfo redisOnlineInfo = new RedisOnlineInfo();
            redisOnlineInfo.setStation(jsonStation);
            redisOnlineInfo.setJsonRegistry(jsonRegistry);
            redisOnlineInfo.setLoginParam(jsonLoginParam);
            redisOnlineInfo.setVpn(vpn);

            result = redisUtils.set(key, redisOnlineInfo, registryTimeout);

            if (!result) {
                //todo log 写redis失败
            }
        }

        if (redisUtils.hasKey(key)) {
            redisUtils.expire(key, registryTimeout);
            RedisOnlineInfo redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
            List<JsonDevice> curList = new ArrayList<>();
            List<JsonDevice> deviceList = deviceDao.getListByFsuId(redisOnlineInfo.getFsuId());
            JSONArray array = request.getJSONArray("devList");
            for (int i = 0; i < array.size(); ++i) {
                curList.add(new JsonDevice(array.getString(i)));
            }
            deviceMatchService.matchingDevice(deviceList, curList);

            deviceDao.deleteListByFsuId(redisOnlineInfo.getFsuId());
            deviceDao.insertListByFsuId(deviceList);

            redisOnlineInfo.setInnerIp(jsonRegistry.getInnerIp());
            redisOnlineInfo.setInnerPort(jsonRegistry.getInnerPort());
            result = redisUtils.set(key, redisOnlineInfo, registryTimeout);
        }

        checkOnline(jsonRegistry.getSn(), request);

        return result;
    }

    /**
     * 解析内部服务上报的注册信息
     * @param request 上报信息
     * @return 注册信息
     */
    private JsonRegistry getRegistryInfo(JSONObject request) {
        JsonRegistry jsonRegistry = JSONObject.parseObject(request.toJSONString(), JsonRegistry.class);
        JSONArray array = request.getJSONArray("devList");
        for (int i = 0; i < array.size(); ++i) {
            String[] dev = array.get(i).toString().split("-");

            JsonDevice jsonDevice = new JsonDevice();
            jsonDevice.setType(Integer.parseInt(dev[0]));
            jsonDevice.setPort(dev[1]);
            jsonDevice.setResNo(Integer.parseInt(dev[3]));

            jsonRegistry.getDeviceList().add(jsonDevice);
        }
        return jsonRegistry;
    }

    /**
     * 接收内部服务上报数据信息
     * @param sn sn
     * @param request 上报数据信息
     * @return 处理结果
     */
    public boolean rcvData(String sn, JSONObject request) {
        boolean result = false;

        RedisOnlineInfo redisOnlineInfo = getRedisOnlineInfo(sn);
        if (redisOnlineInfo == null) {
            //终端在redis中不存在，说明离线，返回false
            return result;
        }

        String fsuId = redisOnlineInfo.getFsuId();
        List<JsonDevice> jsonDeviceList = deviceDao.getListByFsuId(fsuId);
        if (jsonDeviceList.size() == 0) {
            //若该fsuId下没有设备id，则不需处理，直接返回true
            return true;
        }

        JSONArray array = request.getJSONArray("data");
        if (array.size() == 0) {
            //若没有设备信息，返回true
            return true;
        }

        for (int i = 0; i < array.size(); ++i) {
            JSONObject data = array.getJSONObject(i);
            String[] dev = data.getString("dev").split("-");
            int type = Integer.parseInt(dev[0]);
            int resNo = Integer.parseInt(dev[1]);

            String deviceId = getDeviceId(type, resNo, jsonDeviceList);

            if (deviceId == null){
                //该设备未找到对应铁塔设备Id，跳过
                continue;
            }

            RedisData redisData = getRedisData(fsuId, deviceId);

            JSONObject info = data.getJSONObject("info");
            for (String signalId : info.keySet()) {
                Signal signal = getSignal(type, signalId);
                if (signal != null) {
                    redisData.getValues().put(signal.getCntbId(), info.getString(signalId));
                }
            }
            result = redisUtils.set(RedisTable.getDataKey(fsuId, deviceId), redisData);
        }

        return result;
    }

    /**
     * 获取信号点信息
     * @param type 内部设备类型
     * @param signalId 内部信号点id
     * @return 信号点信息
     */
    private Signal getSignal(int type, String signalId) {
        Signal result;
        String key = RedisTable.getSignalIdKey(type, signalId);
        if (!redisUtils.hasKey(key)) {
            result = signalDao.getInfoByTypeAndSignalId(type, signalId);
            redisUtils.set(key, result);
        } else {
            result = redisUtils.get(key, Signal.class);
        }

        return result;
    }

    /**
     * 接收内部服务上报FSU信息
     * @param sn sn
     * @param request 上报信息
     * @return 处理结果
     */
    public boolean rcvFsuInfo(String sn, JSONObject request) {
        boolean result = false;

        RedisOnlineInfo redisOnlineInfo = getRedisOnlineInfo(sn);
        if (redisOnlineInfo == null) {
            //终端在redis中不存在，说明离线，返回false
            return result;
        }

        String fsuId = redisOnlineInfo.getFsuId();

        List<JsonDevice> jsonDeviceList = deviceDao.getListByFsuId(fsuId);
        if (jsonDeviceList.size() == 0) {
            //若该fsuId下没有设备id，则不需处理，直接返回true
            return true;
        }

        RedisData redisData = getRedisData(fsuId, fsuId);

        if (request.containsKey("cpuUse")) {
            redisData.getValues().put("0438101001", request.getString("cpuUse"));
        }
        if (request.containsKey("memUse")) {
            redisData.getValues().put("0438102001", request.getString("memUse"));
        }
        if (request.containsKey("sysTime")) {
            redisData.getValues().put("0438103001", request.getString("sysTime"));
        }
        if (request.containsKey("csq")) {
            redisData.getValues().put("0438104001", request.getString("csq"));
        }
        result = redisUtils.set(RedisTable.getDataKey(fsuId, fsuId), redisData);

        return result;
    }

    /**
     * 获取redis中的数据信息
     * @param fsuId fsuId
     * @param deviceId deviceId
     * @return 数据信息，若不存在，则新建
     */
    private RedisData getRedisData(String fsuId, String deviceId) {
        RedisData result = new RedisData();
        String dataKey = RedisTable.getDataKey(fsuId, deviceId);
        if (redisUtils.hasKey(dataKey)) {
            result = redisUtils.get(dataKey, RedisData.class);
        }
        result.setDeviceId(deviceId);
        return result;
    }

    /**
     * 解析内部服务上报的告警信息
     * @param request 上报数据信息
     * @return 处理结果
     */
    public boolean rcvAlarm(String sn, JSONObject request) {
        boolean result = false;

        RedisOnlineInfo redisOnlineInfo = getRedisOnlineInfo(sn);
        if (redisOnlineInfo == null) {
            //终端在redis中不存在，说明离线，返回false
            return result;
        }

        String fsuId = redisOnlineInfo.getFsuId();
        List<JsonDevice> jsonDeviceList = deviceDao.getListByFsuId(fsuId);
        if (jsonDeviceList.size() == 0) {
            //若该fsuId下没有设备id，则不需处理，直接返回false
            return false;
        }

        JSONArray array = request.getJSONArray("alarmList");

        for (int i = 0; i < array.size(); ++i) {
            JSONObject jsonObject = array.getJSONObject(i);

            String[] dev = jsonObject.getString("dev").split("-");
            int type = Integer.parseInt(dev[0]);
            int resNo = Integer.parseInt(dev[1]);

            String deviceId = getDeviceId(type, resNo, jsonDeviceList);
            if (deviceId == null) {
                logger.info("ALARM_REGISTER: 查找铁塔设备Id失败,type:" + type + ",resNo:" + resNo);
                continue;
            }

            Alarm alarm = getAlarm(type, jsonObject.getString("alarmId"));
            if (alarm == null) {
                logger.info("ALARM_REGISTER: 查找铁塔告警点失败,type:" + type + ",alarmId:" + jsonObject.getString("alarmId"));
                continue;
            }

            RedisAlarm redisAlarm = getRedisAlarm(alarm, jsonObject, fsuId, deviceId);
            if (redisAlarm == null) {
                continue;
            }

            if (!alarmLogDao.upsert(redisAlarm)) {
                logger.error("ALARM_REGISTER: 数据库记录失败,redisAlarm:" + JSONObject.toJSONString(redisAlarm));
                continue;
            }

            String alarmKey = RedisTable.getAlarmKey(fsuId, String.valueOf(redisAlarm.getSerialNo()));
            RedisAlarm curRedisAlarm = redisUtils.get(alarmKey, RedisAlarm.class);
            if (curRedisAlarm != null) {
                curRedisAlarm.setAlarmFlag(redisAlarm.getAlarmFlag());
                curRedisAlarm.setEndTime(redisAlarm.getEndTime());
            } else {
                curRedisAlarm = redisAlarm;
            }
            if (!redisUtils.set(alarmKey, curRedisAlarm)) {
                logger.error("ALARM_REGISTER: redis记录失败,redisAlarm:" + JSONObject.toJSONString(redisAlarm));
                continue;
            }
        }

        result = true;

        return result;
    }

    /**
     * 获取告警点对应模板
     * @param type 内部类型
     * @param alarmId 告警点Id
     * @return 告警点模板信息
     */
    private Alarm getAlarm(int type, String alarmId) {
        Alarm result;

        String key = RedisTable.getAlarmIdKey(type, alarmId);
        result = redisUtils.get(key, Alarm.class);
        if (result == null) {
            result = alarmDao.getAlarmByTypeAndId(type, alarmId);
            redisUtils.set(key, result);
        }

        return result;
    }

    /**
     * 处理告警信息
     * @param alarm 告警点模板信息
     * @param info 告警信息
     * @param fsuId fsuId
     * @param deviceId deviceId
     * @return 告警信息
     */
    private RedisAlarm getRedisAlarm(Alarm alarm, JSONObject info,
                                     String fsuId, String deviceId) {
        RedisAlarm result = new RedisAlarm();
        if ((info.getInteger("link") & 32) > 0) {
            result.setAlarmFlag(false);
        } else if ((info.getInteger("link") & 16) > 0) {
            result.setAlarmFlag(true);
        } else {
            logger.info("ALARM_REGISTER: 无法识别告警标识,link:" + info.getInteger("link"));
            return null;
        }

        result.setHighFrequency(info.getInteger("highRate") == 1);
        result.setValue(info.getString("value"));

        result.setFsuId(fsuId);
        result.setDeviceId(deviceId);
        result.setSerialNo(String.format("%010d", info.getInteger("num")));
        result.setId(alarm.getCntbId());
        result.setAlarmLevel(alarm.getLevel());
        result.setAlarmDesc(alarm.getDesc());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.setStartTime(simpleDateFormat.format(info.getLongValue("tReport")));
        if (info.containsKey("tRecover")) {
            result.setEndTime(simpleDateFormat.format(info.getLongValue("tRecover")));
        }

        result.setStartReported(false);
        result.setEndReported(false);
        result.setReportCount(0);

        return result;
    }

    /**
     * 获取当前sn在redis中的信息
     * @param sn sn
     * @return 若在线，返回信息，若不在线，返回null
     */
    private RedisOnlineInfo getRedisOnlineInfo(String sn) {
        RedisOnlineInfo result = null;

        String key = RedisTable.getRegistryKey(sn);
        if (!redisUtils.hasKey(key)) {
            //终端在redis中不存在，说明离线，返回false
            return result;
        }

        result = redisUtils.get(key, RedisOnlineInfo.class);

        return result;
    }

    /**
     * 在指定设备列表中查找类型和资源编号对应的铁塔设备Id
     * @param type 内部设备类型
     * @param resNo 资源编号
     * @param list 当前设备列表
     * @return 铁塔设备Id
     */
    private String getDeviceId(int type, int resNo, List<JsonDevice> list) {
        String result = null;
        for (int j = 0; j < list.size(); ++j) {
            JsonDevice device = list.get(j);
            if (device.getType() == type && device.getResNo() == resNo) {
                result = device.getDeviceId();
                break;
            }
        }

        return result;
    }

    /**
     * 接收内部服务上报的解绑信息
     * @param sn sn
     * @param request 上报信息
     * @return 处理结果
     */
    public boolean fsuUnbind(String sn, JSONObject request) {
        boolean result;

        JsonStation jsonStation = stationDao.getInfoBySn(sn);

        String fsuId = jsonStation.getFsuId();

        String key = RedisTable.getRegistryKey(sn);
        if (redisUtils.hasKey(key)) {
            redisUtils.del(key);
        }
        if (redisUtils.hasKey(RedisTable.getFsuBindKey(fsuId))) {
            redisUtils.del(RedisTable.getFsuBindKey(fsuId));
        }
        Set<String> list = redisUtils.keys(RedisTable.getDataKey(fsuId, "*"));
        for (String dataKey : list) {
            redisUtils.del(dataKey);
        }

        result = stationDao.unbindByFsuIdAndSn(sn, fsuId);

        return result;
    }

    /**
     * 铁塔平台获取Fsu信息
     * @param request 请求信息
     * @return 回复报文字符串
     */
    public String cntbGetFsuInfo(String request) {
        String result = null;

        try {
            GetFsuInfo getFsuInfo = (GetFsuInfo)MessageUtil.stringToMessage(request, GetFsuInfo.class);

            if (!checkTowerOnline(getFsuInfo.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(getFsuInfo.getFsuId());

            RedisData redisData = redisUtils.get(RedisTable.getDataKey(getFsuInfo.getFsuId(), getFsuInfo.getFsuId()), RedisData.class);

            GetFsuInfoAck ack = new GetFsuInfoAck();
            ack.setFsuId(getFsuInfo.getFsuId());
            ack.setFsuCode(getFsuInfo.getFsuId());
            ack.setCpu("0");
            ack.setMemu("0");
            if (redisData != null && redisData.getValues().containsKey("0438101001")) {
                ack.setCpu(redisData.getValues().get("0438101001").toString());
            }
            if (redisData != null && redisData.getValues().containsKey("0438102001")) {
                ack.setMemu(redisData.getValues().get("0438102001").toString());
            }
            ack.setResult(1);
            result = getXmlMsg(new MessageResp(ack));
        } catch (JAXBException e) {
            logger.error("CntbGetFsuInfo: 解析请求报文失败：" + request);
        }

        return result;
    }

    /**
     * 铁塔平台时间同步
     * @param request 请求信息
     * @return 回复报文字符串
     */
    public String cntbTimeCheck(String request) {
        String result = null;

        TimeCheckAck ack = new TimeCheckAck();
        ack.setResult(1);
        result = getXmlMsg(new MessageResp(ack));

        return result;
    }

    /**
     * 铁塔平台获取数据
     * @param request 请求信息
     * @return 回复报文字符串
     */
    public String cntbGetData(String request) {
        String result = null;

        try {
            GetData getData = (GetData)MessageUtil.stringToMessage(request, GetData.class);

            if (!checkTowerOnline(getData.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(getData.getFsuId());

            RedisFsuBind redisFsuBind = getFsuBindInfo(getData.getFsuId());

            Map<String, List<Signal>> deviceSignalMap = getDeviceSignalMap(getData, redisFsuBind);

            GetDataAck ack = new GetDataAck();
            ack.setFsuId(getData.getFsuId());
            ack.setFsuCode(getData.getFsuId());
            ack.getValue().getDeviceListList().add(new XmlList());
            ack.setResult(1);
            for (String deviceId : deviceSignalMap.keySet()) {
                RedisData redisData = redisUtils.get(RedisTable.getDataKey(getData.getFsuId(), deviceId), RedisData.class);

                Device device = new Device();
                device.setId(deviceId);
                device.setCode(deviceId);
                device.settSemaphoreList(new ArrayList<>());
                for (Signal signal : deviceSignalMap.get(deviceId)) {
                    TSemaphore tSemaphore = new TSemaphore();
                    tSemaphore.setId(signal.getCntbId());
                    tSemaphore.setType(signal.getIdType());
                    tSemaphore.setMeasuredVal("0");
                    tSemaphore.setSetupVal("0");
                    tSemaphore.setStatus(0);
                    if (redisData != null && redisData.getValues().containsKey(signal.getCntbId())) {
                        tSemaphore.setMeasuredVal(redisData.getValues().get(signal.getCntbId()).toString());
                        tSemaphore.setSetupVal(redisData.getValues().get(signal.getCntbId()).toString());
                    }
                    device.gettSemaphoreList().add(tSemaphore);
                }
                ack.getValue().getDeviceListList().get(0).getDeviceList().add(device);
            }

            result = getXmlMsg(new MessageResp(ack));
        } catch (JAXBException e) {
            logger.error("CntbGetData: 解析请求报文失败,request:" + request);
        }

        return result;
    }

    /**
     * 获取指定信息中的设备ID与信号点
     * @param getData 指定信息
     * @param redisFsuBind redis中设备绑定信息
     * @return 设备ID与信号点
     */
    private Map<String, List<Signal>> getDeviceSignalMap(GetData getData, RedisFsuBind redisFsuBind) {
        Map<String, List<Signal>> result = new HashMap<>();

        List<String> curDeviceIdList = redisFsuBind.getDeviceIdList();
        for (int i = 0; i < getData.getDeviceList().getDeviceList().size(); ++i) {
            //遍历请求设备ID
            String deviceId = getData.getDeviceList().getDeviceList().get(i).getId();
            if (deviceId.equals("99999999999999")) {
                //若当前请求设备ID为“99999999999999”，则将该FSU下所有设备ID与信号点ID全加入到Map中，并直接跳出循环
                for (int j = 0; j < curDeviceIdList.size(); ++j){
                    List<Signal> signalList = getSignalListByDeviceId(curDeviceIdList.get(j));
                    result.put(curDeviceIdList.get(j), signalList);
                }
                break;
            }
            if (curDeviceIdList.contains(deviceId) && !result.containsKey(deviceId)) {
                //若当前设备ID列表存在待获取设备ID，且当前Map中不存在该设备ID
                result.put(deviceId, new ArrayList<>());
                List<Signal> signalList = getSignalListByDeviceId(deviceId);
                for (int j = 0; j < getData.getDeviceList().getDeviceList().get(i).getIdList().size(); ++j) {
                    //遍历请求信号点ID
                    String signalId = getData.getDeviceList().getDeviceList().get(i).getIdList().get(j);

                    if (signalId.equals("9999999999")) {
                        //若当前请求信号点ID为“9999999999”，则将该设备下的所有信号点加入Map中
                        result.put(deviceId, signalList);
                        continue;
                    }

                    for (Signal signal : signalList) {
                        if (signal.getCntbId().equals(signalId)) {
                            result.get(deviceId).add(signal);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 获取指定铁塔设备ID下的所有信号点
     * @param deviceId 铁塔设备ID
     * @return 信号点列表
     */
    private List<Signal> getSignalListByDeviceId(String deviceId) {
        List<Signal> result = new ArrayList<>();
        String cntbType = deviceMatchService.getCntbType(deviceId);
        DevType devType = devTypeDao.getInfoByCntbType(cntbType);
        if (devType != null) {
            result = signalDao.getListByType(devType.getType());
        }

        return result;
    }

    /**
     * 铁塔平台设置数据点
     * @param request 请求信息
     * @return 回复报文字符串
     */
    public String cntbSetPoint(String request) {
        String result= null;

        try {
            SetPoint setPoint = (SetPoint)MessageUtil.stringToMessage(request, SetPoint.class);

            if (!checkTowerOnline(setPoint.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(setPoint.getFsuId());

            RedisFsuBind redisFsuBind = getFsuBindInfo(setPoint.getFsuId());
            RedisOnlineInfo redisOnlineInfo = redisUtils.get(RedisTable.getRegistryKey(redisFsuBind.getSn()), RedisOnlineInfo.class);

            SetPointAck ack = new SetPointAck();
            ack.setFsuId(setPoint.getFsuId());
            ack.setFsuCode(setPoint.getFsuId());
            for (Device device : setPoint.getValue().getDeviceListList().get(0).getDeviceList()) {
                Device resultDevice = new Device();
                resultDevice.setId(device.getId());
                resultDevice.setCode(device.getCode());
                resultDevice.setSuccessList(new SetResultList());
                resultDevice.setFailList(new SetResultList());

                for (TSemaphore tSemaphore : device.gettSemaphoreList()) {
                    JsonDevice jsonDevice = deviceDao.getInfoByFsuIdAndDeviceId(setPoint.getFsuId(), device.getId());
                    if (jsonDevice == null || jsonDevice.getPort() == null) {
                        resultDevice.getFailList().getIdList().add(tSemaphore.getId());
                        continue;
                    }
                    Signal signal = signalDao.getInfoByTypeAndCntbId(jsonDevice.getType(), tSemaphore.getId());
                    if (signal == null || signal.getSignalId() == null) {
                        resultDevice.getFailList().getIdList().add(tSemaphore.getId());
                        continue;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("dev", jsonDevice.getType() + "-" + jsonDevice.getResNo());
                    jsonObject.put("setPoint", Integer.valueOf(signal.getSignalId()));
                    jsonObject.put("setData", Float.valueOf(tSemaphore.getSetupVal()));

                    ModuleMsg msg = new ModuleMsg(PktType.SET_DATA, redisOnlineInfo.getSn());
                    msg.setPayload(jsonObject);

                    RpcNotifyProto.RpcMessage resMsg = sendMsg(redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort(), msg);
                    if (resMsg == null) {
                        resultDevice.getFailList().getIdList().add(tSemaphore.getId());
                        continue;
                    }
                    JSONObject responseObject = JSONObject.parseObject(resMsg.getPayload());
                    if (responseObject.containsKey("result") && (responseObject.getInteger("result") == 1)) {
                        resultDevice.getSuccessList().getIdList().add(tSemaphore.getId());
                    } else {
                        resultDevice.getFailList().getIdList().add(tSemaphore.getId());
                    }
                }
                ack.getDeviceList().add(resultDevice);
                ack.setResult(1);
                result = getXmlMsg(new MessageResp(ack));
            }

        } catch (JAXBException e) {
            logger.error("CntbSetPoint: 解析请求报文失败,request:" + request);
        }

        return result;
    }

    /**
     * 铁塔平台获取门限值
     * @param request 请求信息
     * @return 回复报文字符串
     */
    public String cntbGetThreshold(String request) {
        String result = null;

        try {
            GetThreshold getThreshold = (GetThreshold)MessageUtil.stringToMessage(request, GetThreshold.class);

            if (!checkTowerOnline(getThreshold.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(getThreshold.getFsuId());

            RedisFsuBind redisFsuBind = getFsuBindInfo(getThreshold.getFsuId());
            RedisOnlineInfo redisOnlineInfo = redisUtils.get(RedisTable.getRegistryKey(redisFsuBind.getSn()), RedisOnlineInfo.class);

            Map<String, List<Alarm>> deviceAlarmMap = getDeviceThresholdMap(getThreshold, redisFsuBind);

            List<JsonDevice> jsonDeviceList = deviceDao.getListByFsuId(getThreshold.getFsuId());

            GetThresholdAck getThresholdAck = new GetThresholdAck();
            getThresholdAck.setFsuId(getThreshold.getFsuId());
            getThresholdAck.setFsuCode(getThreshold.getFsuId());
            getThresholdAck.getValue().getDeviceListList().add(new XmlList());

            for (String deviceId : deviceAlarmMap.keySet()) {
                //遍历待获取的设备
                List<Alarm> alarmList = deviceAlarmMap.get(deviceId);

                //初始化该设备下所有门限值信息
                Device device = new Device();
                device.setId(deviceId);
                device.setCode(deviceId);
                device.settThresholdList(new ArrayList<>());
                getThresholdAck.getValue().getDeviceListList().get(0).getDeviceList().add(device);

                for (JsonDevice jsonDevice : jsonDeviceList) {
                    //遍历数据库中保存的设备列表，判断和当前获取设备是否为同一设备
                    if (!jsonDevice.getDeviceId().equals(deviceId)) {
                        continue;
                    }
                    //若是同一设备，则判断该设备在内部服务中是否存在，若不存在则直接跳过该设备
                    if (jsonDevice.getPort() == null) {
                        continue;
                    }
                    //如果端口号不为null，说明该设备存在，向内部服务发送请求获取该设备下的告警点值
                    ModuleMsg msg = getGetThresholdRequest(redisFsuBind.getSn(), jsonDevice, alarmList);
                    RpcNotifyProto.RpcMessage resMsg = sendMsg(redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort(), msg);
                    if (resMsg != null && JSON.parse(resMsg.getPayload()) instanceof JSONArray) {
                        JSONArray responseArray = JSONObject.parseArray(resMsg.getPayload());
                        for (Alarm alarm : alarmList) {
                            TThreshold tThreshold = new TThreshold();
                            tThreshold.setId(alarm.getCntbId());
                            tThreshold.setType(alarm.getType());
                            tThreshold.setThreshold("0");
                            tThreshold.setAbsoluteVal("0.0");
                            tThreshold.setRelativeVal("0.0");
                            tThreshold.setStatus(0);
                            for (int i = 0; i < responseArray.size(); ++i) {
                                //遍历报文中的告警点信息
                                JSONObject value = responseArray.getJSONObject(i);
                                if (alarm.getAlarmId().equals(value.getString("alarmId"))) {
                                    getThresholdAck.setResult(1);
                                    //与当前默认门限值匹配，并修改默认门限值
                                    tThreshold.setThreshold(value.getString("threshold"));
                                    break;
                                }
                            }
                            device.gettThresholdList().add(tThreshold);
                        }
                    }
                    break;
                }
            }

            if (getThresholdAck.getValue().getDeviceListList().get(0).getDeviceList().size() == 0) {
                return result;
            }

            result = getXmlMsg(new MessageResp(getThresholdAck));

        } catch (JAXBException e) {
            logger.error("CntbGetThreshold: 解析请求报文失败,request:" + request);
        }

        return result;
    }

    /**
     * 获取指定信息中的设备ID与告警点
     * @param getThreshold 指定信息
     * @param redisFsuBind redis中设备绑定信息
     * @return 设备ID与告警点
     */
    private Map<String, List<Alarm>> getDeviceThresholdMap(GetThreshold getThreshold, RedisFsuBind redisFsuBind) {
        Map<String, List<Alarm>> result = new HashMap<>();

        List<String> curDeviceIdList = redisFsuBind.getDeviceIdList();
        for (int i = 0; i < getThreshold.getDeviceList().getDeviceList().size(); ++i) {
            //遍历请求设备ID
            String deviceId = getThreshold.getDeviceList().getDeviceList().get(i).getId();
            if (deviceId.equals("99999999999999")) {
                //若当前请求设备ID为“99999999999999”，则将该FSU下所有设备ID与告警点ID全加入到Map中，并直接跳出循环
                for (int j = 0; j < curDeviceIdList.size(); ++j){
                    List<Alarm> alarmList = getAlarmListByDeviceId(curDeviceIdList.get(j));
                    result.put(curDeviceIdList.get(j), alarmList);
                }
                break;
            }
            if (curDeviceIdList.contains(deviceId) && !result.containsKey(deviceId)) {
                //若当前设备ID列表存在待获取设备ID，且当前Map中不存在该设备ID
                result.put(deviceId, new ArrayList<>());
                List<Alarm> alarmList = getAlarmListByDeviceId(deviceId);
                for (int j = 0; j < getThreshold.getDeviceList().getDeviceList().get(i).getIdList().size(); ++j) {
                    //遍历请求告警点ID
                    String alarmId = getThreshold.getDeviceList().getDeviceList().get(i).getIdList().get(j);

                    if (alarmId.equals("9999999999")) {
                        //若当前请求信号点ID为“9999999999”，则将该设备下的所有信号点加入Map中
                        result.put(deviceId, alarmList);
                        continue;
                    }

                    for (Alarm alarm : alarmList) {
                        if (alarm.getCntbId().equals(alarmId)) {
                            result.get(deviceId).add(alarm);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 获取指定铁塔设备ID下的所有告警点
     * @param deviceId 铁塔设备ID
     * @return 告警点列表
     */
    private List<Alarm> getAlarmListByDeviceId(String deviceId) {
        List<Alarm> result = new ArrayList<>();
        String cntbType = deviceMatchService.getCntbType(deviceId);
        DevType devType = devTypeDao.getInfoByCntbType(cntbType);
        if (devType != null) {
            result = alarmDao.getListByType(devType.getType());
        }

        return result;
    }

    /**
     * 获取Xml报文
     * @param message 注册信息
     * @return Xml报文字符串
     */
    private String getXmlMsg(MessageResp message) {
        String result = null;

        try {
            result = MessageUtil.messageToString(message);
        } catch (Exception ex) {
            logger.error("GetXmlMsg: 实体类转xml失败,msg:" + JSONObject.toJSONString(message));
        }

        return result;
    }

    /**
     * 获取获取告警点配置报文
     * @param sn sn
     * @param jsonDevice 设备信息
     * @param alarmList 告警点列表
     * @return 报文
     */
    private ModuleMsg getGetThresholdRequest(String sn, JsonDevice jsonDevice, List<Alarm> alarmList) {
        ModuleMsg result = new ModuleMsg(PktType.GET_ALARM_PARAM, sn);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", jsonDevice.getType());
        jsonObject.put("resNo", jsonDevice.getResNo());
        jsonObject.put("port", jsonDevice.getPort());

        List<String> alarmIdList = new ArrayList<>();
        for (Alarm alarm : alarmList) {
            alarmIdList.add(alarm.getAlarmId());
        }
        jsonObject.put("alarmId", alarmIdList);
        result.setPayload(jsonObject);

        return result;
    }

    /**
     * 铁塔平台设置数据点
     * @param request 请求信息
     * @return 回复报文字符串
     */
    public String cntbSetThreshold(String request) {
        String result= null;

        try {
            SetThreshold setThreshold = (SetThreshold)MessageUtil.stringToMessage(request, SetThreshold.class);

            if (!checkTowerOnline(setThreshold.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(setThreshold.getFsuId());

            RedisFsuBind redisFsuBind = getFsuBindInfo(setThreshold.getFsuId());
            RedisOnlineInfo redisOnlineInfo = redisUtils.get(RedisTable.getRegistryKey(redisFsuBind.getSn()), RedisOnlineInfo.class);

            SetThresholdAck ack = new SetThresholdAck();
            ack.setFsuId(setThreshold.getFsuId());
            ack.setFsuCode(setThreshold.getFsuId());
            for (Device device : setThreshold.getValue().getDeviceListList().get(0).getDeviceList()) {
                Device resultDevice = new Device();
                resultDevice.setId(device.getId());
                resultDevice.setCode(device.getCode());
                resultDevice.setSuccessList(new SetResultList());
                resultDevice.setFailList(new SetResultList());

                for (TThreshold tThreshold : device.gettThresholdList()) {
                    JsonDevice jsonDevice = deviceDao.getInfoByFsuIdAndDeviceId(setThreshold.getFsuId(), device.getId());
                    if (jsonDevice == null || jsonDevice.getPort() == null) {
                        resultDevice.getFailList().getIdList().add(tThreshold.getId());
                        continue;
                    }
                    Alarm alarm = alarmDao.getInfoByTypeAndCntbId(jsonDevice.getType(), tThreshold.getId());
                    if (alarm == null || alarm.getAlarmId() == null) {
                        resultDevice.getFailList().getIdList().add(tThreshold.getId());
                        continue;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("dev", jsonDevice.getType() + "-" + jsonDevice.getResNo());
                    jsonObject.put("alarmId", alarm.getAlarmId());
                    jsonObject.put("threshold", Float.valueOf(tThreshold.getThreshold()));

                    ModuleMsg msg = new ModuleMsg(PktType.SET_ALARM_PARAM, redisOnlineInfo.getSn());
                    msg.setPayload(jsonObject);

                    RpcNotifyProto.RpcMessage resMsg = sendMsg(redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort(), msg);
                    if (resMsg == null) {
                        resultDevice.getFailList().getIdList().add(tThreshold.getId());
                        continue;
                    }

                    JSONObject responseObject = JSONObject.parseObject(resMsg.getPayload());
                    if (responseObject.containsKey("result") && (responseObject.getInteger("result") == 1)) {
                        resultDevice.getSuccessList().getIdList().add(tThreshold.getId());
                    } else {
                        resultDevice.getFailList().getIdList().add(tThreshold.getId());
                    }
                }
                ack.getDeviceList().add(resultDevice);
                ack.setResult(1);
                result = getXmlMsg(new MessageResp(ack));
            }

        } catch (JAXBException e) {
            logger.error("CntbSetThreshold: 解析请求报文失败,request:" + request);
        }

        return result;
    }

    /**
     * 向其他服务发送信息
     * @param ip ip
     * @param port port
     * @param msg 请求信息
     * @return 回复信息
     */
    private RpcNotifyProto.RpcMessage sendMsg(String ip, int port, ModuleMsg msg) {
        Configuration conf = new Configuration();
        RpcClient rpcClient = new RpcClient(conf);
        RpcModuleBase rpcModuleBase = new RpcModuleBase(rpcClient);

        RpcNotifyProto.RpcMessage response = null;
        try {
            response = rpcModuleBase.postMsg("", new InetSocketAddress(ip, port), JSONObject.toJSONString(msg));
        } catch (IOException e) {
            logger.error("sendRpcMsg: 发送Rpc请求失败,ip:" + ip + ",port:" + port + ",msg:" + msg);
        }
        return response;
    }

    /**
     * 刷新最后一次接收铁塔报文时间
     * @param fsuId fsuId
     */
    private void refreshLastTimeRecvTowerMsg(String fsuId) {
        RedisFsuBind redisFsuBind = getFsuBindInfo(fsuId);
        if (redisFsuBind == null) {
            return;
        }

        String key = RedisTable.getRegistryKey(redisFsuBind.getSn());

        RedisOnlineInfo redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
        if (redisOnlineInfo != null) {
            redisOnlineInfo.setLastTimeRecvTowerMsg(System.currentTimeMillis() / 1000);
            long time = redisUtils.getExpire(key);
            redisUtils.set(key, redisOnlineInfo, time);
        }
    }

    /**
     * 检查指定fsu是否铁塔平台在线
     * @param fsuId fsuId
     * @return 在线状态
     */
    private boolean checkTowerOnline(String fsuId) {
        RedisFsuBind redisFsuBind = getFsuBindInfo(fsuId);
        if (redisFsuBind == null) {
            //未查询到该fsuId有绑定sn记录，返回null
            return false;
        }

        String key = RedisTable.getRegistryKey(redisFsuBind.getSn());
        if (!redisUtils.hasKey(key)) {
            //未查询到该fsuId对应的终端在线，返回null
            return false;
        }

        RedisOnlineInfo redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
        if (!redisOnlineInfo.isOnline()) {
            //该fsuId对应的终端在线，但铁塔平台离线，返回null
            return false;
        }

        long time = redisUtils.getExpire(key);
        redisOnlineInfo.setLastTimeRecvTowerMsg(System.currentTimeMillis()/1000);
        redisUtils.set(key, redisOnlineInfo, time);

        return true;
    }

    /**
     * 获取fsuId对应绑定信息
     * @param fsuId fsuId
     * @return 绑定信息，若为绑定，则为null
     */
    private RedisFsuBind getFsuBindInfo(String fsuId) {
        RedisFsuBind result = null;

        String key = RedisTable.getFsuBindKey(fsuId);
        result = redisUtils.get(key, RedisFsuBind.class);
        if (result == null) {
            JsonStation jsonStation = stationDao.getInfoByFsuId(fsuId);
            if (jsonStation != null) {
                List<JsonDevice> jsonDeviceList = deviceDao.getListByFsuId(fsuId);
                result = new RedisFsuBind();
                result.setFsuId(fsuId);
                result.setSn(jsonStation.getSn());
                for (JsonDevice jsonDevice : jsonDeviceList) {
                    result.getDeviceIdList().add(jsonDevice.getDeviceId());
                }
                redisUtils.set(key, result);
            }
        }

        return result;
    }

    /**
     * 刷新redis中的在线信息
     * @param sn sn
     * @param innerIp 内部服务ip
     * @param innerPort 内部服务端口
     */
    private void refreshRedisOnlineInfo(String sn, String innerIp, int innerPort) {
        String key = RedisTable.getRegistryKey(sn);

        RedisOnlineInfo redisOnlineInfo;
        if (!redisUtils.hasKey(key)) {
            JsonStation jsonStation = stationDao.getInfoBySn(sn);
            if (jsonStation == null) {
                return;
            }
            JsonLoginParam jsonLoginParam = loginParamDao.getInfoByFsuId(jsonStation.getFsuId());
            Vpn vpn = vpnDao.getInfoByName(jsonStation.getVpnName());

            redisOnlineInfo = new RedisOnlineInfo();
            redisOnlineInfo.setSn(sn);
            redisOnlineInfo.setStation(jsonStation);
            redisOnlineInfo.setLoginParam(jsonLoginParam);
            redisOnlineInfo.setVpn(vpn);
        } else {
            redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
        }
        redisOnlineInfo.setInnerIp(innerIp);
        redisOnlineInfo.setInnerPort(innerPort);
        redisUtils.set(key, redisOnlineInfo, registryTimeout);
    }

    /**
     * 检查终端是否铁塔在线，若离线，启动线程执行注册流程
     * @param sn sn
     * @return 终端在redis中的状态是否正常
     */
    public void checkOnline(String sn, JSONObject request) {
        String key = RedisTable.getRegistryKey(sn);

        refreshRedisOnlineInfo(sn, request.getString("innerIp"), request.getInteger("innerPort"));

        if (redisUtils.hasKey(key)) {
            RedisOnlineInfo redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
            if (System.currentTimeMillis()/1000 - redisOnlineInfo.getLastTimeRecvTowerMsg() >
                    redisOnlineInfo.getHeartbeatInterval() * redisOnlineInfo.getHeartbeatTimeoutLimit()) {
                //若当前时间 - 上次收到铁塔报文时间 > 心跳间隔 * 心跳超时次数，说明铁塔平台离线
                redisOnlineInfo.setOnline(false);
                long time = redisUtils.getExpire(key);
                redisUtils.set(key, redisOnlineInfo, time);
            }
        }

        if (redisUtils.hasKey(key)) {
            RedisOnlineInfo redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
            logger.info("-----------------------login pre-----------------------" + JSONObject.toJSONString(redisOnlineInfo));
            if (!redisOnlineInfo.isOnline() &&
                    (redisOnlineInfo.getLastTimeLogin() + redisOnlineInfo.getLoginInterval() < System.currentTimeMillis() / 1000) &&
                    redisUtils.hasKey(RedisTable.VPN_HASH) &&
                    redisUtils.hHasKey(RedisTable.VPN_HASH, redisOnlineInfo.getLocalName()) &&
                    (!redisOnlineInfo.isLogining())) {
                long time = redisUtils.getExpire(key);
                redisOnlineInfo.setLogining(true);
                redisUtils.set(key, redisOnlineInfo, time);
                logger.info("-----------------------login start-----------------------" + JSONObject.toJSONString(redisOnlineInfo));
                //若铁塔离线且本地VPN连接正常，启动线程执行注册流程
                taskExecutor.execute(new CntbLoginService(sn,
                        towerGatewayHostname, towerGatewayPort, rpcModule, redisUtils, rpcClient,
                        carrierDao));
                System.out.println(taskExecutor.getActiveCount());
            }
        }
    }

    /**
     * 检查是否需要上报告警
     * @param sn sn
     */
    public void checkAlarm(String sn) {
        taskExecutor.execute(new CntbAlarmService(sn,
                towerGatewayHostname, towerGatewayPort, rpcModule, redisUtils, rpcClient, alarmLogDao));
    }
}
