package com.kongtrolink.framework.utils;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.RedisTable;
import com.kongtrolink.framework.execute.module.dao.*;
import com.kongtrolink.framework.execute.module.model.*;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonStation;
import com.kongtrolink.framework.service.DeviceMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommonUtils {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    DeviceMatchService deviceMatchService;

    @Autowired
    StationDao stationDao;
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
    HisDataDao hisDataDao;

    //终端在线超时时间(秒)
    @Value("${tower.registry.timeout}")
    private int registryTimeout;

    public String getRedisLock(String sn, int timeout) {
        String key = RedisTable.getLockKey(sn);
        return redisUtils.lock(key, timeout);
    }

    public void redisUnlock(String sn, String value) {
        String key = RedisTable.getLockKey(sn);
        redisUtils.unlock(key, value);
    }

    /**
     * 获取当前sn在redis中的信息
     * @param sn sn
     * @return 若在线，返回信息，若不在线，返回null
     */
    public RedisOnlineInfo getRedisOnlineInfo(String sn) {
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
     * 设置当前sn在redis中的信息
     * @param redisOnlineInfo 信息
     * @return 设置结果
     */
    public boolean setRedisOnlineInfo(RedisOnlineInfo redisOnlineInfo) {
        String key = RedisTable.getRegistryKey(redisOnlineInfo.getSn());

        long time = redisUtils.getExpire(key);
        return redisUtils.set(key, redisOnlineInfo, time);
    }

    /**
     * 设置当前sn在redis中的信息
     * @param redisOnlineInfo 信息
     * @return 设置结果
     */
    public boolean insertRedisOnlineInfo(RedisOnlineInfo redisOnlineInfo) {
        String key = RedisTable.getRegistryKey(redisOnlineInfo.getSn());

        return redisUtils.set(key, redisOnlineInfo, registryTimeout);
    }

    /**
     * 删除当前sn在redis中的信息
     * @param sn sn
     */
    public void delRedisOnlineInfo(String sn) {
        String key = RedisTable.getRegistryKey(sn);
        if (redisUtils.hasKey(key)) {
            redisUtils.del(key);
        }
    }

    /**
     * 获取fsuId对应绑定信息
     * @param fsuId fsuId
     * @return 绑定信息，若为绑定，则为null
     */
    public RedisFsuBind getRedisFsuBind(String fsuId) {
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
     * 设置fsu绑定信息
     * @param redisFsuBind fsu绑定信息
     * @return 设置结果
     */
    public boolean setRedisFsuBind(RedisFsuBind redisFsuBind) {
        String key = RedisTable.getFsuBindKey(redisFsuBind.getFsuId());

        return redisUtils.set(key, redisFsuBind);
    }

    /**
     * 删除当前fsuId对应的绑定关系
     * @param fsuId fsuId
     */
    public void delRedisFsuBind(String fsuId) {
        String key = RedisTable.getFsuBindKey(fsuId);
        if (redisUtils.hasKey(key)) {
            redisUtils.del(key);
        }
    }

    /**
     * 获取信号点信息
     * @param type 内部设备类型
     * @param signalId 内部信号点id
     * @return 信号点信息
     */
    public Signal getSignal(int type, String signalId) {
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
     * 获取redis中的数据信息
     * @param fsuId fsuId
     * @param deviceId deviceId
     * @return 数据信息，若不存在，则新建
     */
    public RedisData getRedisData(String fsuId, String deviceId) {
        RedisData result = new RedisData();

        String key = RedisTable.getDataKey(fsuId, deviceId);
        if (redisUtils.hasKey(key)) {
            result = redisUtils.get(key, RedisData.class);
        }
        result.setDeviceId(deviceId);

        return result;
    }

    /**
     * 设置数据信息
     * @param fsuId fsuId
     * @param redisData 数据信息
     * @return 设置结果
     */
    public boolean setRedisData(String fsuId, RedisData redisData) {
        String key = RedisTable.getDataKey(fsuId, redisData.getDeviceId());

        return redisUtils.set(key, redisData);
    }

    /**
     * 删除指定fsuId下指定deviceId的所有数据信息
     * @param fsuId fsuId
     * @param deviceId deviceId
     */
    public void delRedisData(String fsuId, String deviceId) {
        String key = RedisTable.getDataKey(fsuId, deviceId);
        if (redisUtils.hasKey(key)) {
            redisUtils.del(key);
        }
    }

    /**
     * 删除指定fsuId对应的所有数据信息
     * @param fsuId fsuId
     */
    public void delRedisData(String fsuId) {
        Set<String> list = redisUtils.keys(RedisTable.getDataKey(fsuId, "*"));
        for (String dataKey : list) {
            redisUtils.del(dataKey);
        }
    }

    /**
     * 获取指定fsuId下的历史数据记录信息
     * @param fsuId fsuId
     * @return 记录信息，不存在则为null
     */
    public String getHisRedisData(String fsuId) {

        String key = RedisTable.getHisDataKey(fsuId);
        if (redisUtils.hasKey(key)) {
            return redisUtils.getString(key);
        }
        return null;
    }

    /**
     * 设置指定fsuId对应的历史数据记录信息
     * @param fsuId fsuId
     * @param value 记录信息
     * @return 设置结果
     */
    public boolean setHisRedisData(String fsuId, long value) {
        String key = RedisTable.getHisDataKey(fsuId);

        return redisUtils.set(key, value);
    }

    /**
     * 删除指定fsuId对应的历史数据记录信息
     * @param fsuId fsuId
     */
    public void delHisRedisData(String fsuId) {
        String key = RedisTable.getHisDataKey(fsuId);
        if (redisUtils.hasKey(key)) {
            redisUtils.del(key);
        }
    }

    /**
     * 获取历史数据信息
     * @param fsuId fsuId
     * @param deviceId deviceId
     * @param signalId signalId
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 历史数据信息
     */
    public List<HisData> getHisDataList(String fsuId, String deviceId,
                                        String signalId, long startTime, long endTime) {
        return hisDataDao.getList(fsuId, deviceId, signalId, startTime, endTime);
    }

    /**
     * 获取指定fsuId对应的活动告警信息
     * @param fsuId fsuId
     * @param serialNo 序列号
     * @return 告警信息
     */
    public RedisAlarm getRedisAlarm(String fsuId, String serialNo) {
        RedisAlarm redisAlarm = null;

        String key = RedisTable.getAlarmKey(fsuId, serialNo);
        if (redisUtils.hasKey(key)) {
            redisAlarm = redisUtils.get(key, RedisAlarm.class);
        }

        return redisAlarm;
    }

    /**
     * 获取指定fsuId对应的活动告警列表
     * @param fsuId fsuId
     * @return 活动告警列表
     */
    public List<RedisAlarm> getRedisAlarmList(String fsuId) {
        List<RedisAlarm> result = new ArrayList<>();

        Set<String> list = redisUtils.keys(RedisTable.getAlarmKey(fsuId, "*"));
        for (String key : list) {
            RedisAlarm redisAlarm = redisUtils.get(key, RedisAlarm.class);

            result.add(redisAlarm);
        }

        return result;
    }

    /**
     * 设置redis中的告警信息
     * @param redisAlarm 告警信息
     * @return 设置结果
     */
    public boolean setRedisAlarm(RedisAlarm redisAlarm) {
        String key = RedisTable.getAlarmKey(redisAlarm.getFsuId(), redisAlarm.getSerialNo());

        return redisUtils.set(key, redisAlarm);
    }

    /**
     * 删除指定fsuId下的指定告警
     * @param fsuId fsuId
     * @param serialNo 序列号
     */
    public void delRedisAlarm(String fsuId, String serialNo) {
        String key = RedisTable.getAlarmKey(fsuId, serialNo);
        if (redisUtils.hasKey(key)) {
            redisUtils.del(key);
        }
    }

    /**
     * 获取指定内部设备下的所有信号点信息
     * @param type 内部设备类型
     * @return 信号点列表
     */
    public List<Signal> getListByType(int type) {
        return signalDao.getListByType(type);
    }

    /**
     * 获取指定内部类型下的指定信号点信息
     * @param type 内部设备类型
     * @param signalId 内部信号点Id
     * @return 信号点信息
     */
    public Signal getSignalByTypeAndSignalId(int type, String signalId) {
        return signalDao.getInfoByTypeAndSignalId(type, signalId);
    }

    /**
     * 获取指定内部类型下的指定铁塔信号点信息
     * @param type 内部设备类型
     * @param cntbId 铁塔信号点Id
     * @return 信号点信息
     */
    public Signal getSignalByTypeAndCntbId(int type, String cntbId) {
        return signalDao.getInfoByTypeAndCntbId(type, cntbId);
    }

    /**
     * 获取信号点类型名称
     * @param type 信号点类型
     * @return 名称
     */
    public String getSignalTypeName(int type) {
        switch (type) {
            case 2:
                return "DI";
            case 3:
                return "AI";
            case 4:
                return "DO";
            case 5:
                return "AO";
            default:
                return "";
        }
    }

    /**
     * 获取指定类型设备下的所有告警点信息
     * @param type 设备类型
     * @return 告警点信息列表
     */
    public List<Alarm> getAlarmListByType(int type) {
        return alarmDao.getListByType(type);
    }

    /**
     * 获取告警点对应模板
     * @param type 内部类型
     * @param alarmId 告警点Id
     * @return 告警点模板信息
     */
    public Alarm getAlarm(int type, String alarmId) {
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
     * 获取指定类型与铁塔Id的告警点信息
     * @param type 内部设备类型
     * @param cntbId 铁塔门限Id
     * @return 告警点信息
     */
    public Alarm getAlarmByTypeAndCntbId(int type, String cntbId) {
        return alarmDao.getInfoByTypeAndCntbId(type, cntbId);
    }

    /**
     * 获取VpnIp信息
     * @param localName vpn连接名称
     * @return ip
     */
    public String getVpnIp(String localName) {

        if (redisUtils.hasKey(RedisTable.VPN_HASH) &&
                redisUtils.hHasKey(RedisTable.VPN_HASH, localName)) {
            return redisUtils.hget(RedisTable.VPN_HASH, localName).toString();
        }
        return null;
    }

    /**
     * 获取上报铁塔运营商类型
     * @param type 内部服务上报类型
     * @return 上报铁塔类型
     */
    public String getCarrier(String type) {
        if (type == null) {
            return "";
        }
        if (!redisUtils.hasKey(RedisTable.CARRIER_HASH) ||
                !redisUtils.hHasKey(RedisTable.CARRIER_HASH, type)) {
            Map<String, String> map = carrierDao.getAll();
            if (map.containsKey(type)) {
                Map<String, Object> tmp = new HashMap<>();
                //遍历map中的键
                for (String key : map.keySet()) {
                    tmp.put(key, map.get(key));
                }
                redisUtils.hmset(RedisTable.CARRIER_HASH, tmp, 0);
                return map.get(type);
            } else {
                return "";
            }
        }
        return redisUtils.hget(RedisTable.CARRIER_HASH, type).toString();
    }

    /**
     * 通过铁塔设备类型获取对应信息列表
     * @param cntbType 铁塔设备类型
     * @return 对应信息列表，若不存在则数量为0
     */
    public List<DevType> getDevTypeByCntbType(String cntbType) {
        List<DevType> result = new ArrayList<>();
        String keyPattern = RedisTable.getDevTypeKey("*", cntbType);
        Set<String> keySet = redisUtils.keys(keyPattern);
        if (keySet.size() > 0) {
            for (String key : keySet) {
                result.add(redisUtils.get(key, DevType.class));
            }
        } else {
            DevType devType = devTypeDao.getInfoByCntbType(cntbType);
            if (devType != null) {
                result.add(devType);
                redisUtils.set(RedisTable.getDevTypeKey(
                        String.valueOf(devType.getType()), cntbType), devType);
            }
            //找不到对应设备类型，返回空list
        }

        return result;
    }

    /**
     * 通过内部设备类型获取对应信息
     * @param type 内部设备类型
     * @return 对应信息，若不存在则为null
     */
    public DevType getDevTypeByType(int type) {
        String keyPattern = RedisTable.getDevTypeKey(
                String.valueOf(type),  "*");
        Set<String> keySet = redisUtils.keys(keyPattern);
        String[] keyArray = new String[keySet.size()];
        keySet.toArray(keyArray);
        if (keyArray.length == 1) {
            return redisUtils.get(keyArray[0], DevType.class);
        }

        redisUtils.del(keyArray);
        DevType result = devTypeDao.getInfoByType(type);
        if (result == null) {
            //找不到对应设备类型，返回null
            return null;
        }

        redisUtils.set(RedisTable.getDevTypeKey(
                String.valueOf(type), result.getCntbType()), result);
        return result;
    }
}
