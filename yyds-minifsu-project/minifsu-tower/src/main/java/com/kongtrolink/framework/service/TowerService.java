package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fengw
 * 处理铁塔业务逻辑
 * 新建文件 2019-4-15 17:04:42
 */
@Service
public class TowerService {

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
     * @param request 绑定信息
     * @return 绑定结果
     */
    public boolean fsuBind(JSONObject request) {
        boolean result = false;

        JsonStation jsonStation = getStationInfo(request);

        //更新数据库中的注册参数
        JsonLoginParam jsonLoginParam = getLoginParam(request);

        //更新数据库中的设备列表
        JSONArray array = request.getJSONArray("deviceList");
        List<JsonDevice> jsonDeviceList = getDeviceList(array, jsonStation.getFsuId());

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
     * @param array 接收到的设备列表
     * @param fsuId fsuId
     */
    private List<JsonDevice> getDeviceList(JSONArray array, String fsuId) {
        List<JsonDevice> result = new ArrayList<>();
        for (int i = 0; i < array.size(); ++i) {
            result.add(new JsonDevice(fsuId, array.getString(i)));
        }
        //取出对应设备ID列表，重新进行设备匹配，删除原有设备信息后将新设备信息写入数据库
        List<JsonDevice> curDeviceList = deviceDao.getListByFsuId(fsuId);
        deviceMatchService.matchingDevice(result, curDeviceList);
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
                if (!unbindResult) {
                    //todo 若解绑失败？

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
        redisUtils.hset(RedisTable.FSU_BIND_HASH, jsonStation.getFsuId(), redisFsuBind);
    }

    /**
     * 平台注册终端信息
     * @param request 请求信息
     * @return
     */
    public boolean login(JSONObject request) {
        boolean result = false;

        JsonRegistry jsonRegistry = getRegistryInfo(request);
        String key = RedisTable.getRegistryKey(jsonRegistry.getSn());

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
        }

        checkOnline(jsonRegistry.getSn());

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
     * @param request 上报数据信息
     * @return 处理结果
     */
    public boolean rcvData(JSONObject request) {
        boolean result = false;

        String sn = request.getString("SN");
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
     * @param request 上报信息
     * @return 处理结果
     */
    public boolean rcvFsuInfo(JSONObject request) {
        boolean result = false;

        String sn = request.getString("SN");
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
        if (request.containsKey("signalStrength")) {
            redisData.getValues().put("0438104001", request.getString("signalStrength"));
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
        result.setDeviceId(fsuId);
        return result;
    }

    /**
     * 解析内部服务上报的告警信息
     * @param request 上报数据信息
     * @return 处理结果
     */
    public boolean rcvAlarm(JSONObject request) {
        boolean result = false;

        String sn = request.getString("sN");
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

            String[] dev = jsonObject.getString("dev_colId").split("_")[0].split("-");
            int type = Integer.parseInt(dev[0]);
            int resNo = Integer.parseInt(dev[1]);

            String deviceId = getDeviceId(type, resNo, jsonDeviceList);
            if (deviceId == null) {
                //todo 在日志中记录错误代码和原始数据，该告警无对应设备Id
                continue;
            }

            Alarm alarm = getAlarm(type, jsonObject.getString("alarmId"));
            if (alarm == null) {
                //todo 在日志中记录错误代码和原始数据，未获取到告警点对应信息
                continue;
            }

            RedisAlarm redisAlarm = getRedisAlarm(alarm, jsonObject, fsuId, deviceId);
            if (redisAlarm == null) {
                continue;
            }

            if (!alarmLogDao.upsert(redisAlarm)) {
                //todo 在日志中记录错误代码和原始数据，数据库中记录出错
                continue;
            }

            String alarmKey = RedisTable.getAlarmKey(fsuId, String.valueOf(redisAlarm.getSerialNo()));
            RedisAlarm curRedisAlarm = redisUtils.get(alarmKey, RedisAlarm.class);
            if (curRedisAlarm != null) {
                curRedisAlarm.setEndTime(redisAlarm.getEndTime());
            } else {
                curRedisAlarm = redisAlarm;
            }
            if (!redisUtils.set(alarmKey, curRedisAlarm)) {
                //todo 在日志中记录错误代码和原始数据，redis中记录出错
                continue;
            }
        }

        checkAlarm(sn);

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
    private RedisAlarm getRedisAlarm(Alarm alarm, JSONObject info, String fsuId, String deviceId) {
        RedisAlarm result = new RedisAlarm();
        if ((info.getInteger("link") & 32) > 0) {
            result.setAlarmFlag(true);
        } else if ((info.getInteger("link") & 16) > 0) {
            result.setAlarmFlag(false);
        } else {
            //todo 在日志中记录错误代码和原始数据，无法识别告警标识
            return null;
        }

        result.setHighFrequency(info.getInteger("h") == 1);
        result.setValue(info.getString("value"));

        result.setFsuId(fsuId);
        result.setDeviceId(deviceId);
        result.setSerialNo(info.getInteger("num"));
        result.setId(alarm.getCntbId());
        result.setAlarmLevel(alarm.getLevel());
        result.setAlarmDesc(alarm.getDesc());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.setStartTime(simpleDateFormat.format(info.getInteger("tReport")));
        result.setEndTime(simpleDateFormat.format(info.getInteger("tRecover")));

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
        redisUtils.set(key, result, registryTimeout); //刷新redis中的超时时间

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
     * @param request 上报信息
     * @return 处理结果
     */
    public boolean fsuUnbind(JSONObject request) {
        boolean result;

        String sn = request.getString("sn");
        String fsuId = request.getString("fsuId");

        String key = RedisTable.getRegistryKey(sn);
        if (redisUtils.hasKey(key)) {
            redisUtils.del(key);
        }
        if (redisUtils.hHasKey(RedisTable.FSU_BIND_HASH, fsuId)) {
            redisUtils.hdel(RedisTable.FSU_BIND_HASH, fsuId);
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
            e.printStackTrace();
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

            JSONObject jsonRedisFsuBind = (JSONObject)redisUtils.hget(RedisTable.FSU_BIND_HASH, getData.getFsuId());
            RedisFsuBind redisFsuBind = JSONObject.parseObject(jsonRedisFsuBind.toJSONString(), RedisFsuBind.class);

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
            e.printStackTrace();
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
     * 获取Xml报文
     * @param message 注册信息
     * @return Xml报文字符串
     */
    private String getXmlMsg(MessageResp message) {
        String result = null;

        try {
            result = MessageUtil.messageToString(message);
        } catch (Exception ex) {
            //todo 实体类转xml失败
        }

        return result;
    }

    /**
     * 检查指定fsu是否铁塔平台在线
     * @param fsuId fsuId
     * @return 在线状态
     */
    private boolean checkTowerOnline(String fsuId) {
        if (!redisUtils.hHasKey(RedisTable.FSU_BIND_HASH, fsuId)) {
            //未查询到该fsuId有绑定sn记录，返回null
            return false;
        }

        JSONObject jsonRedisFsuBind = (JSONObject)redisUtils.hget(RedisTable.FSU_BIND_HASH, fsuId);
        RedisFsuBind redisFsuBind = JSONObject.parseObject(jsonRedisFsuBind.toJSONString(), RedisFsuBind.class);

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
     * 检查终端是否铁塔在线，若离线，启动线程执行注册流程
     * @param sn sn
     * @return 终端在redis中的状态是否正常
     */
    private void checkOnline(String sn) {
        String key = RedisTable.getRegistryKey(sn);
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
            if (!redisOnlineInfo.isOnline() &&
                redisUtils.hasKey(RedisTable.VPN_HASH) &&
                redisUtils.hHasKey(RedisTable.VPN_HASH, redisOnlineInfo.getLocalName())) {
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
    private void checkAlarm(String sn) {
        taskExecutor.execute(new CntbAlarmService(sn,
                towerGatewayHostname, towerGatewayPort, rpcModule, redisUtils, rpcClient, alarmLogDao));
    }
}
