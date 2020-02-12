/**
 * *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************
 */
package com.kongtrolink.framework.scloud.entity.realtime;

import com.kongtrolink.framework.scloud.entity.SignalType;

import java.io.Serializable;
import java.util.Date;

/**
 * 信号，实时值
 */
public class SignalInfoEntity implements Serializable{

    private static final long serialVersionUID = 906984305026134245L;

    private String name;
    private String type;    // 类型 [遥测 遥信 遥控 遥调]
    private String value;   //实时值
    private String measurement; //单位
    private double downValue;//针对遥控保存下发值
    private Double threshold;  // 门限值
    private String alarmLevel;      //告警等级
    private String cntbId; //铁塔的10位ID

    public void init(SignalType signalType,Object value) {
        this.name = signalType.getTypeName();
        this.type = signalType.getType();
        this.alarmLevel = signalType.getAlarmLevel();
        this.cntbId = signalType.getCntbId();
        this.measurement = signalType.getMeasurement();
        if(value!=null){
            this.value = String.valueOf(value);
        }

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public double getDownValue() {
        return downValue;
    }

    public void setDownValue(double downValue) {
        this.downValue = downValue;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }
}
