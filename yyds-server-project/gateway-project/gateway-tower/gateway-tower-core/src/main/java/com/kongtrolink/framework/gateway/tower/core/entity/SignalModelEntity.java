package com.kongtrolink.framework.gateway.tower.core.entity;

import java.io.Serializable;

/**
 * Mag
 * by Mag on 2019/6/3.
 */
public class SignalModelEntity implements Serializable{

    private static final long serialVersionUID = -6659384726026036931L;
    private String code;
    private String deviceCode;
    private String name;
    private String type;
    private String unit;
    private String alarmLevel;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

}
