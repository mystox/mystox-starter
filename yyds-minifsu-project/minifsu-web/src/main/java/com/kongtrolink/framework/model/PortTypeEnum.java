package com.kongtrolink.framework.model;

/**
 * Created by mystoxlol on 2019/5/16, 9:14.
 * company: kongtrolink
 * description:
 * update record:
 */
public enum PortTypeEnum {
    COM(1),
    CAN(6),
    EPLC(0),
    DI(3),
    AI(2),
    DO(4);


    private int value;

    PortTypeEnum(int value) {
        this.value = value;
    }

    public static int getValue(String name) {
        for (PortTypeEnum portTypeEnum : PortTypeEnum.values()) {
            if(portTypeEnum.name().equals(name))
                return portTypeEnum.value;
        }
        return -1;
    }


    public static String getName(int type) {
        for (PortTypeEnum portTypeEnum : PortTypeEnum.values()) {
            if(portTypeEnum.value == type)
                return portTypeEnum.name();
        }
        return "";
    }


    public static void main(String[] args)
    {
        System.out.println(PortTypeEnum.AI);
    }


}
