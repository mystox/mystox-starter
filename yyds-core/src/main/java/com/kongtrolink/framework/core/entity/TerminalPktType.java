package com.kongtrolink.framework.core.entity;

/**
 * Created by mystoxlol on 2019/3/27, 11:09.
 * company: kongtrolink
 * description:
 * update record:
 */
public enum TerminalPktType
{

    HEART(0, "heart"), //SN上报注册
    REGISTRY(1, "registry"), //SN上报注册
    TERMINAL_REPORT(2, "terminal_report"), //终端信息上报
    DEV_LIST(3, "dev_list"), //上报终端接入设备信息
    DATA_CHANGE(4,"data_change"),//终端数据变化上报(类似告警)
    DATA_REPORT(5,"data_report"),//终端数据变化上报
    TERMINAL_REBOOT(6,"terminal_reboot"),//终端数据变化上报
    SET_GPRS(7,"set_gprs"),//设置GPRS服务器
    SET_DATA(8,"set_data"),//设置信号点值
    UPGRADE(9,"upgrade"),//升级请求
    FILE_GET(10,"file_get"),//文件流传输
    RUN_STATE(11,"run_state");//文件流传输

    private final int key;
    private final String value;

    public int getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    TerminalPktType(int key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public static String toValue(int key) {
        for (TerminalPktType item : TerminalPktType.values()) {
            if (item.key == key) {
                return item.value;
            }
        }
        return null;
    }
    public static int toKey(String value) {
        for (TerminalPktType item : TerminalPktType.values()) {
            if (item.value .equals(value)) {
                return item.key;
            }
        }
        return -1;
    }
}
