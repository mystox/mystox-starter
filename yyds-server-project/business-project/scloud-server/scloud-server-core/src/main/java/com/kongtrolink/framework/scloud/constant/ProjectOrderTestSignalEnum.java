package com.kongtrolink.framework.scloud.constant;

/**
 * 工程管理-测试单-测试项 测试信号点 枚举
 * Created by Eric on 2020/4/21.
 */
public enum ProjectOrderTestSignalEnum {

    SWITCH_POWER_016("0406016001", "交流输入XX停电告警"),SWITCH_POWER_022("0406022001", "防雷器故障告警"),SWITCH_POWER_024("0406024001", "整流模块XX故障告警"),
    SWITCH_POWER_101("0406101001", "交流输入XX相电压Ua"),SWITCH_POWER_102("0406102001", "交流输入XX相电压Ub"),
    SWITCH_POWER_103("0406103001", "交流输入XX相电压Uc"),SWITCH_POWER_111("0406111001", "直流电压"),SWITCH_POWER_112("0406112001", "直流负载总电流"),SWITCH_POWER_113("0406113001", "整流模块XX电流"),

    BATTERY_102("0407102001", "总电压"),BATTERY_106("0407106001", "前半组电压"),BATTERY_107("0407107001", "后半组电压"),

    GENERAL_AIR_CONDITIONER_102("0415102001", "回风温度"),

    AC_SMART_METER_104("0416104001", "A相电压Ua"),AC_SMART_METER_105("0416105001", "B相电压Ub"),AC_SMART_METER_106("0416106001", "C相电压Uc"),

    WATER_SENSOR_001("0418001001", "水浸告警"),SMOKE_SENSOR_002("0418002001", "烟雾告警"), INFRARED_SENSOR_003("0418003001", "红外告警"),
    TEMPERATURE_SENSOR_101("0418101001", "环境温度"), HUMIDITY_SENSOR_102("0418102001", "环境湿度");

    private final String cntbId;
    private final String signalName;

    ProjectOrderTestSignalEnum(String cntbId, String signalName){
        this.cntbId = cntbId;
        this.signalName = signalName;
    }

    public String getCntbId() {
        return cntbId;
    }

    public String getSignalName() {
        return signalName;
    }
}
