package com.kongtrolink.framework.core.entity;

/**
 * Created by mystoxlol on 2019/3/27, 11:09.
 * company: kongtrolink
 * description:
 * update record:
 */
public enum TerminalPktType
{

    HEART(0, "heart","心跳"), //SN上报注册
    REGISTRY(1, "registry","注册"), //SN上报注册
    TERMINAL_REPORT(2, "terminal_report","终端属性"), //终端信息上报
    DEV_LIST(3, "dev_list","接入设备"), //上报终端接入设备信息
    DATA_CHANGE(4,"data_change","变化数据"),//终端数据变化上报(类似告警)
    DATA_REPORT(5,"data_report","实时数据"),//终端数据实时上报
    TERMINAL_REBOOT(6,"terminal_reboot","重启"),//终端重启
    SET_GPRS(8,"set_gprs","设置GPRS"),//设置GPRS服务器
    SET_DATA(7,"set_data_terminal","设置信号点值"),//设置信号点值
    UPGRADE(9,"upgrade","升级请求"),//升级请求
    FILE_GET(10,"file_get","文件获取"),//文件流传输
    RUN_STATE(11,"run_status","运行状态");//文件流传输

    private final int key;
    private final String value;
    private final String name;

    public int getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }

    TerminalPktType(int key, String value, String name)
    {
        this.key = key;
        this.value = value;
        this.name = name;
    }

    public static String toValue(int key) {
        for (TerminalPktType item : TerminalPktType.values()) {
            if (item.key == key) {
                return item.value;
            }
        }
        return null;
    }
    public static String toName(int key) {
        for (TerminalPktType item : TerminalPktType.values()) {
            if (item.key == key) {
                return item.name;
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
