package com.kongtrolink.framework.gateway.tower.core.constant;

/**
 * xx
 * by Mag on 2019/8/12.
 */
public class RedisKey {
    //FSU信息 key: FSU_INFO_uniqueCode
    public static final String FSU_INFO = "gw_fsu_info";
    //FSU 心跳离线统计
    public static final String FSU_HEART_OFF = "gw_fsu_heartOff";
    //告警
    public static final String FSU_ALARM_INFO = "gw_fsu_alarmInfo";
    //上报给资管的设备存储
    public static final String ASSENT_DEVICE_TYPE = "gw_assentReport_devType";
    //设备跟FSU的绑定关系 key:uniqueCode#deviceId value:FsuId
    public static final String ASSENT_DEVICE_INFO = "gw_assentReport_device";
    //FSU跟设备的绑定关系 key:uniqueCode#FsuId value: List<String>
    public static final String ASSENT_FSU_INFO = "gw_assentReport_fsu";
}
