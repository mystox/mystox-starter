package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.StateCode;
import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.entity.RedisTable;
import com.kongtrolink.framework.entity.xml.base.MessageResp;
import com.kongtrolink.framework.entity.xml.msg.GetData;
import com.kongtrolink.framework.entity.xml.msg.GetFsuInfo;
import com.kongtrolink.framework.entity.xml.msg.*;
import com.kongtrolink.framework.entity.xml.util.MessageUtil;
import com.kongtrolink.framework.execute.module.dao.*;
import com.kongtrolink.framework.execute.module.model.*;
import com.kongtrolink.framework.jsonType.JsonStation;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonLoginParam;
import com.kongtrolink.framework.utils.CommonUtils;
import com.kongtrolink.framework.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CommonUtils commonUtils;
    @Autowired
    StationDao stationDao;
    @Autowired
    VpnDao vpnDao;
    @Autowired
    LoginParamDao loginParamDao;
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    CarrierDao carrierDao;
    @Autowired
    AlarmDao alarmDao;
    @Autowired
    AlarmLogDao alarmLogDao;
    @Autowired
    HisDataDao hisDataDao;
    @Autowired
    RpcService rpcService;
    @Autowired
    LogUtils logUtils;
    @Autowired
    CntbLoginService cntbLoginService;
    @Autowired
    CntbAlarmService cntbAlarmService;
    @Autowired
    DeviceMatchService deviceMatchService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

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
    //历史数据保存间隔(分钟)
    @Value("${tower.loginParam.dataSaveInterval}")
    private int dataSaveInterval;

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
            if (!fsuUnbind(curStation.getSn())) {
                logUtils.saveLog("", curStation.getSn(), PktType.FSU_BIND, StateCode.MONGO_ERROR);
                logger.error("TERMINAL_UNBIND: 原终端解绑失败,sn:" + curStation.getSn() + ",fsuId:" + curStation.getFsuId());
                return result;
            }
        }

        updateBindRedis(jsonStation, jsonLoginParam, jsonDeviceList);

        //更新基站信息
        result = updateStationBindDB(jsonStation, jsonLoginParam, jsonDeviceList);

        // 若重新绑定，需重新向铁塔平台注册
        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
        if (redisOnlineInfo != null) {
            redisOnlineInfo.setOnline(false);
            commonUtils.setRedisOnlineInfo(redisOnlineInfo);
        }

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
        if (!request.containsKey("dataSaveInterval")) {
            loginParam.setDataSaveInterval(this.dataSaveInterval);
        }
        return loginParam;
    }

    /**
     * 获取设备列表
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
                commonUtils.delRedisOnlineInfo(curStation.getSn());
                if (!unbindResult) {
                    logUtils.saveLog("", curStation.getSn(), PktType.FSU_BIND, StateCode.MONGO_ERROR);
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

        RedisOnlineInfo onlineInfo = commonUtils.getRedisOnlineInfo(jsonStation.getSn());
        String key = RedisTable.getRegistryKey(jsonStation.getSn());
        if (onlineInfo != null) {
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

            commonUtils.setRedisOnlineInfo(onlineInfo);
        }

        RedisFsuBind redisFsuBind = new RedisFsuBind();
        redisFsuBind.setSn(jsonStation.getSn());
        redisFsuBind.setFsuId(jsonStation.getFsuId());
        for(JsonDevice device : jsonDeviceList) {
            redisFsuBind.getDeviceIdList().add(device.getDeviceId());
        }
        //更新redis中fsuId和sn的映射关系
        commonUtils.setRedisFsuBind(redisFsuBind);
    }

    /**
     * 接收内部服务上报数据信息
     * @param sn sn
     * @param request 上报数据信息
     * @return 处理结果
     */
    public boolean rcvData(String sn, JSONObject request) {
        boolean result = false;

        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
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

            RedisData redisData = commonUtils.getRedisData(fsuId, deviceId);

            JSONObject info = data.getJSONObject("info");
            for (String signalId : info.keySet()) {
                Signal signal = commonUtils.getSignal(type, signalId);
                if (signal != null) {
                    redisData.getValues().put(signal.getCntbId(), info.getString(signalId));
                }
            }
            result = commonUtils.setRedisData(fsuId, redisData);
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

        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
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

        RedisData redisData = commonUtils.getRedisData(fsuId, fsuId);

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
        result = commonUtils.setRedisData(fsuId, redisData);

        return result;
    }

    /**
     * 解析内部服务上报的告警信息
     * @param request 上报数据信息
     * @return 处理结果
     */
    public boolean rcvAlarm(String sn, JSONObject request) {
        boolean result = false;

        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
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

            Alarm alarm = commonUtils.getAlarm(type, jsonObject.getString("alarmId"));
            if (alarm == null) {
                logger.info("ALARM_REGISTER: 查找铁塔告警点失败,type:" + type + ",alarmId:" + jsonObject.getString("alarmId"));
                continue;
            }

            RedisAlarm redisAlarm = createRedisAlarm(alarm, jsonObject, fsuId, deviceId);
            if (redisAlarm == null) {
                continue;
            }

            if (!alarmLogDao.upsert(redisAlarm)) {
                logUtils.saveLog("", redisOnlineInfo.getSn(), PktType.ALARM_REGISTER, StateCode.MONGO_ERROR);
                logger.error("ALARM_REGISTER: 数据库记录失败,redisAlarm:" + JSONObject.toJSONString(redisAlarm));
                continue;
            }

            RedisAlarm curRedisAlarm = commonUtils.getRedisAlarm(fsuId, redisAlarm.getSerialNo());
            if (curRedisAlarm != null) {
                curRedisAlarm.setAlarmFlag(redisAlarm.getAlarmFlag());
                curRedisAlarm.setEndTime(redisAlarm.getEndTime());
            } else {
                curRedisAlarm = redisAlarm;
            }
            if (!commonUtils.setRedisAlarm(curRedisAlarm)) {
                logUtils.saveLog("", redisOnlineInfo.getSn(), PktType.ALARM_REGISTER, StateCode.REDIS_ERROR);
                logger.error("ALARM_REGISTER: redis记录失败,redisAlarm:" + JSONObject.toJSONString(redisAlarm));
            }
        }

        result = true;

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
    private RedisAlarm createRedisAlarm(Alarm alarm, JSONObject info,
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
     * 在指定设备列表中查找类型和资源编号对应的铁塔设备Id
     * @param type 内部设备类型
     * @param resNo 资源编号
     * @param list 当前设备列表
     * @return 铁塔设备Id
     */
    private String getDeviceId(int type, int resNo, List<JsonDevice> list) {
        String result = null;
        for (JsonDevice device : list) {
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
     * @return 处理结果
     */
    public boolean fsuUnbind(String sn) {
        boolean result;

        JsonStation jsonStation = stationDao.getInfoBySn(sn);

        if (jsonStation != null) {
            String fsuId = jsonStation.getFsuId();

            commonUtils.delRedisOnlineInfo(sn);
            commonUtils.delRedisFsuBind(fsuId);
            commonUtils.delHisRedisData(fsuId);
            commonUtils.delRedisData(fsuId);

            result = stationDao.unbindByFsuIdAndSn(sn, fsuId);
        } else {
            result = true;
        }

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

            if (getFsuInfo == null || isTowerOffline(getFsuInfo.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(getFsuInfo.getFsuId());

            RedisData redisData = commonUtils.getRedisData(getFsuInfo.getFsuId(), getFsuInfo.getFsuId());

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
            logUtils.saveLog("", "", CntbPktTypeTable.GET_FSUINFO, StateCode.XML_ILLEGAL);
            logger.error("CntbGetFsuInfo: 解析请求报文失败：" + request);
        }

        return result;
    }

    /**
     * 铁塔平台时间同步
     * @return 回复报文字符串
     */
    public String cntbTimeCheck() {
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

            if (getData == null || isTowerOffline(getData.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(getData.getFsuId());

            RedisFsuBind redisFsuBind = commonUtils.getRedisFsuBind(getData.getFsuId());

            Map<String, List<Signal>> deviceSignalMap = getDeviceSignalMap(getData, redisFsuBind);

            GetDataAck ack = new GetDataAck();
            ack.setFsuId(getData.getFsuId());
            ack.setFsuCode(getData.getFsuId());
            ack.getValue().getDeviceListList().add(new XmlList());
            ack.setResult(1);
            for (String deviceId : deviceSignalMap.keySet()) {
                RedisData redisData = commonUtils.getRedisData(getData.getFsuId(), deviceId);

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
            logUtils.saveLog("", "", CntbPktTypeTable.GET_DATA, StateCode.XML_ILLEGAL);
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
                for (String curDeviceId : curDeviceIdList) {
                    List<Signal> signalList = getSignalListByDeviceId(curDeviceId);
                    result.put(curDeviceId, signalList);
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
    public List<Signal> getSignalListByDeviceId(String deviceId) {
        List<Signal> result = new ArrayList<>();
        String cntbType = deviceMatchService.getCntbType(deviceId);
        List<DevType> devTypeList = commonUtils.getDevTypeByCntbType(cntbType);
        if (devTypeList.size() > 0) {
            result = commonUtils.getListByType(devTypeList.get(0).getType());
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

            if (setPoint == null || isTowerOffline(setPoint.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(setPoint.getFsuId());

            RedisFsuBind redisFsuBind = commonUtils.getRedisFsuBind(setPoint.getFsuId());
            RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(redisFsuBind.getSn());

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
                    Signal signal = commonUtils.getSignalByTypeAndCntbId(jsonDevice.getType(), tSemaphore.getId());
                    if (signal == null || signal.getSignalId() == null) {
                        resultDevice.getFailList().getIdList().add(tSemaphore.getId());
                        continue;
                    }

                    JSONObject responseObject = rpcService.setData(redisOnlineInfo.getSn(),
                            redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort(),
                            jsonDevice.getType() + "-" + jsonDevice.getResNo(),
                            Integer.valueOf(signal.getSignalId()), Float.valueOf(tSemaphore.getSetupVal()));

                    if (responseObject != null && responseObject.containsKey("result") && (responseObject.getInteger("result") == 1)) {
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
            logUtils.saveLog("", "", CntbPktTypeTable.SET_POINT, StateCode.XML_ILLEGAL);
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

            if (getThreshold == null || isTowerOffline(getThreshold.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(getThreshold.getFsuId());

            RedisFsuBind redisFsuBind = commonUtils.getRedisFsuBind(getThreshold.getFsuId());
            RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(redisFsuBind.getSn());

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
                    List<String> alarmIdList = new ArrayList<>();
                    for (Alarm alarm : alarmList) {
                        alarmIdList.add(alarm.getAlarmId());
                    }
                    JSONArray responseArray = rpcService.getThreshold(redisFsuBind.getSn(), redisOnlineInfo.getInnerIp(),
                                                                    redisOnlineInfo.getInnerPort(), jsonDevice.getType(),
                                                                    jsonDevice.getResNo(), jsonDevice.getPort(), alarmIdList);

                    if (responseArray != null) {
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
            logUtils.saveLog("", "", CntbPktTypeTable.GET_THRESHOLD, StateCode.XML_ILLEGAL);
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
                for (String curDeviceId : curDeviceIdList) {
                    List<Alarm> alarmList = getAlarmListByDeviceId(curDeviceId);
                    result.put(curDeviceId, alarmList);
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
    public List<Alarm> getAlarmListByDeviceId(String deviceId) {
        List<Alarm> result = new ArrayList<>();
        String cntbType = deviceMatchService.getCntbType(deviceId);
        List<DevType> devTypeList = commonUtils.getDevTypeByCntbType(cntbType);
        if (devTypeList.size() > 0) {
            result = commonUtils.getAlarmListByType(devTypeList.get(0).getType());
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
            logUtils.saveLog("", "", "", StateCode.XML_ILLEGAL);
            logger.error("GetXmlMsg: 实体类转xml失败,msg:" + JSONObject.toJSONString(message));
        }

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

            if (setThreshold == null || isTowerOffline(setThreshold.getFsuId())) {
                return result;
            }

            refreshLastTimeRecvTowerMsg(setThreshold.getFsuId());

            RedisFsuBind redisFsuBind = commonUtils.getRedisFsuBind(setThreshold.getFsuId());
            RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(redisFsuBind.getSn());

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
                    Alarm alarm = commonUtils.getAlarmByTypeAndCntbId(jsonDevice.getType(), tThreshold.getId());
                    if (alarm == null || alarm.getAlarmId() == null) {
                        resultDevice.getFailList().getIdList().add(tThreshold.getId());
                        continue;
                    }

                    JSONObject responseObject = rpcService.setThreshold(redisOnlineInfo.getSn(),
                            redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort(),
                            jsonDevice.getType() + "-" + jsonDevice.getResNo(), alarm.getAlarmId(),
                            Float.valueOf(tThreshold.getThreshold()));

                    if (responseObject != null && responseObject.containsKey("result") && (responseObject.getInteger("result") == 1)) {
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
            logUtils.saveLog("", "", CntbPktTypeTable.SET_THRESHOLD, StateCode.XML_ILLEGAL);
            logger.error("CntbSetThreshold: 解析请求报文失败,request:" + request);
        }

        return result;
    }

    /**
     * 刷新最后一次接收铁塔报文时间
     * @param fsuId fsuId
     */
    private void refreshLastTimeRecvTowerMsg(String fsuId) {
        RedisFsuBind redisFsuBind = commonUtils.getRedisFsuBind(fsuId);
        if (redisFsuBind == null) {
            return;
        }

        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(redisFsuBind.getSn());
        if (redisOnlineInfo != null) {
            redisOnlineInfo.setLastTimeRecvTowerMsg(System.currentTimeMillis() / 1000);
            commonUtils.setRedisOnlineInfo(redisOnlineInfo);
        }
    }

    /**
     * 检查指定fsu是否铁塔平台离线
     * @param fsuId fsuId
     * @return 离线状态
     */
    private boolean isTowerOffline(String fsuId) {
        RedisFsuBind redisFsuBind = commonUtils.getRedisFsuBind(fsuId);
        if (redisFsuBind == null) {
            //未查询到该fsuId有绑定sn记录
            return true;
        }

        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(redisFsuBind.getSn());
        if (redisOnlineInfo == null || (!redisOnlineInfo.isOnline())) {
            //该fsuId对应的终端在线，但铁塔平台离线
            return true;
        }

        redisOnlineInfo.setLastTimeRecvTowerMsg(System.currentTimeMillis()/1000);
        commonUtils.setRedisOnlineInfo(redisOnlineInfo);

        return false;
    }

    /**
     * 刷新redis中的在线信息
     * @param sn sn
     * @param innerIp 内部服务ip
     * @param innerPort 内部服务端口
     */
    private void refreshRedisOnlineInfo(String sn, String innerIp, int innerPort) {

        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
        if (redisOnlineInfo == null) {
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

            if (vpn != null) {
                redisOnlineInfo.setVpn(vpn);
            }
        }

        redisOnlineInfo.setInnerIp(innerIp);
        redisOnlineInfo.setInnerPort(innerPort);
        commonUtils.insertRedisOnlineInfo(redisOnlineInfo);
    }

    /**
     * 刷新铁塔在线状态，若离线，启动线程执行注册流程
     * @param sn sn
     */
    public void refreshTowerOnlineInfo(String sn, JSONObject request) {
        String key = RedisTable.getRegistryKey(sn);

        refreshRedisOnlineInfo(sn, request.getString("innerIp"), request.getInteger("innerPort"));

        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
        if (redisOnlineInfo != null) {
            if (System.currentTimeMillis()/1000 - redisOnlineInfo.getLastTimeRecvTowerMsg() >
                    redisOnlineInfo.getHeartbeatInterval() * redisOnlineInfo.getHeartbeatTimeoutLimit()) {
                //若当前时间 - 上次收到铁塔报文时间 > 心跳间隔 * 心跳超时次数，说明铁塔平台离线
                redisOnlineInfo.setOnline(false);
                commonUtils.setRedisOnlineInfo(redisOnlineInfo);
            }
        }

        redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
        if (redisOnlineInfo != null) {
            logger.debug("-----------------------login pre-----------------------" + JSONObject.toJSONString(redisOnlineInfo));
            if (!redisOnlineInfo.isOnline() &&
                    (redisOnlineInfo.getLastTimeLogin() + redisOnlineInfo.getLoginInterval() < System.currentTimeMillis() / 1000) &&
                    (commonUtils.getVpnIp(redisOnlineInfo.getLocalName()) != null) &&
                    (!redisOnlineInfo.isLogining())) {
                redisOnlineInfo.setLogining(true);
                commonUtils.setRedisOnlineInfo(redisOnlineInfo);
                logger.debug("-----------------------login start-----------------------" + JSONObject.toJSONString(redisOnlineInfo));
                //若铁塔离线且本地VPN连接正常，启动线程执行注册流程
                taskExecutor.execute(() -> cntbLoginService.startLogin(sn));
            }
        }
    }

    /**
     * 检查是否需要上报告警
     * @param sn sn
     */
    public void checkAlarm(String sn) {
        taskExecutor.execute(() -> cntbAlarmService.startAlarm(sn));
    }

    /**
     * 保存历史数据
     * @param sn sn
     */
    public void saveHisData(String sn) {
        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
        if (redisOnlineInfo == null) {
            return;
        }
        RedisFsuBind redisFsuBind = commonUtils.getRedisFsuBind(redisOnlineInfo.getFsuId());
        if (redisFsuBind == null) {
            return;
        }

        String hisDataTime = commonUtils.getHisRedisData(redisFsuBind.getFsuId());
        if (hisDataTime != null) {
            long lastTimeSaveData = Integer.valueOf(hisDataTime);
            if (lastTimeSaveData + redisOnlineInfo.getDataSaveInterval() * 60 > System.currentTimeMillis() / 1000) {
                return;
            }
        }

        long time = System.currentTimeMillis();
        commonUtils.setHisRedisData(redisOnlineInfo.getFsuId(), time / 1000);
        List<HisData> list = new ArrayList<>();
        for (String deviceId : redisFsuBind.getDeviceIdList()) {
            RedisData redisData = commonUtils.getRedisData(redisFsuBind.getFsuId(), deviceId);
            List<Signal> signalList = getSignalListByDeviceId(deviceId);
            for (Signal signal : signalList) {
                HisData hisData = new HisData();
                hisData.setFsuId(redisFsuBind.getFsuId());
                hisData.setDeviceId(deviceId);
                hisData.setSignalId(signal.getCntbId());
                hisData.setValue("0");
                hisData.setTime(time);

                if (redisData.getValues().containsKey(signal.getCntbId())) {
                    hisData.setValue(redisData.getValues().get(signal.getCntbId()).toString());
                }
                list.add(hisData);
            }
        }
        hisDataDao.insertList(list);
    }
}
