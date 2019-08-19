package com.kongtrolink.framework.mqtt.entity;

/**
 * Created by mystoxlol on 2019/8/13, 10:38.
 * company: kongtrolink
 * description:
 * update record:
 */
public enum  PayloadType {
    JSON(0, "JSON"),
    STRING(1, "STRING"),
    BYTE(2, "BYTE"),
    ;
    PayloadType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    private int value;
    private String name;
    public static PayloadType valueOf(int value) {
        switch (value) {
            case 0:
                return JSON;
            case 1:
                return STRING;
            case 2:
                return BYTE;
            default:
                return null;
        }
    }
}
