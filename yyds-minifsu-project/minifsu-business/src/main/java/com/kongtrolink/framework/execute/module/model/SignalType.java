package com.kongtrolink.framework.execute.module.model;

/**
 * Created by mystoxlol on 2019/4/15, 18:49.
 * company: kongtrolink
 * description:
 * update record:
 */
public enum SignalType {
    DI("0", "遥信信号"),
    AI("1","遥测信号"),
    DO("2","遥控信号"),
    AO("3","遥调信号"),;


    private String type;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    SignalType(String type, String name) {
        this.type = type;
        this.name = name;
    }


    public static String toName(String type) {
        for (SignalType item : SignalType.values()) {
            if (type.equals(item.getType())) {
                return item.name();
            }
        }
        return null;
    }
}
