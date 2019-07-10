package com.kongtrolink.framework.entity;

public class RedisTable {
    private final static String LOCK = "lock:";
    private final static String CNTBTYPE_TYPE_HASH = "cntbType_type_hash:";
    private final static String CNTBTYPE_SIGNAL_HASH = "cntbType_signal_hash:";
    private final static String CNTBTYPE_ALARM_HASH = "cntbType_alarm_hash:";
    private final static String TERMINAL_HASH = "terminal_hash:";
    private final static String FSU_BIND_HASH = "fsu_bind_hash:";
    public final static String VPN_HASH = "vpn_hash";
    public final static String CARRIER_HASH = "carrier_hash";
    private final static String DATA_HASH = "data_hash:";
    private final static String ALARM_HASH = "alarm_hash:";
    private final static String HIS_DATA_HASH = "his_data:";

    public static String getLockKey(String sn) {
        return RedisTable.LOCK + sn;
    }

    public static String getDevTypeKey(String type, String cntbType) {
        return RedisTable.CNTBTYPE_TYPE_HASH + type + "_" + cntbType;
    }

    /**
     * 获取redis中注册记录的key值
     * @param sn sn
     * @return 注册记录key
     */
    public static String getRegistryKey(String sn) {
        return RedisTable.TERMINAL_HASH + sn;
    }

    /**
     * 获取redis中fsu绑定信息
     * @param fsuId fsuId
     * @return 绑定信息key
     */
    public static String getFsuBindKey(String fsuId) {
        return RedisTable.FSU_BIND_HASH + fsuId;
    }

    /**
     * 获取redis中的数据信息的key
     * @param fsuId fsuId
     * @param deviceId deviceId
     * @return 数据信息key
     */
    public static String getDataKey(String fsuId, String deviceId) {
        return RedisTable.DATA_HASH + fsuId + ":" + deviceId;
    }

    /**
     * 获取redis中信号点信息的key
     * @param type 内部设备类型
     * @param signalId 信号点类型
     * @return 信号点信息key
     */
    public static String getSignalIdKey(int type, String signalId) {
        return RedisTable.CNTBTYPE_SIGNAL_HASH + type + ":" + signalId;
    }

    /**
     * 获取redis中告警点信息的key
     * @param type 内部设备类型
     * @param alarmId 告警点类型
     * @return 告警点信息key
     */
    public static String getAlarmIdKey(int type, String alarmId) {
        return RedisTable.CNTBTYPE_ALARM_HASH + type + ":" + alarmId;
    }

    /**
     * 获取redis中活动告警key
     * @param fsuId fsuId
     * @param serialNo 告警序列号
     * @return 活动告警key
     */
    public static String getAlarmKey(String fsuId, String serialNo) {
        return RedisTable.ALARM_HASH + fsuId + ":" + serialNo;
    }

    /**
     * 获取redis中历史数据保存信息的key
     * @param fsuId fsuId
     * @return 历史数据保存信息key
     */
    public static String getHisDataKey(String fsuId) {
        return RedisTable.HIS_DATA_HASH + fsuId;
    }
}
