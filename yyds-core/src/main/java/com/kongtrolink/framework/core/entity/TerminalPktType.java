package com.kongtrolink.framework.core.entity;

/**
 * Created by mystoxlol on 2019/3/27, 11:09.
 * company: kongtrolink
 * description:
 * update record:
 */
public enum TerminalPktType
{

    REGISTRY(1, "registry"), //SN上报注册
    TERMINAL_REPORT(2, "terminal_report"), //终端信息上报
    DEV_LIST(3, "dev_list"), //上报终端接入设备信息
    DATA_REPORT(4,"data_report");//终端数据变化上报

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
}
