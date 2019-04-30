package com.kongtrolink.gateway.nb.ctcc.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2018/11/23.
 */
public class TestEntity implements Serializable {

    private static final long serialVersionUID = -2174446928991443497L;
    private String deviceId;
    private String name;
    private String type;
    private int age;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
