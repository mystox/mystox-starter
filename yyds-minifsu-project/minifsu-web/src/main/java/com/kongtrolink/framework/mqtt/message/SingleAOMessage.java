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
package com.kongtrolink.framework.mqtt.message;

/**
 * 设置单点遥调信号报文
 * 
 * @author Mosaico
 */
public class SingleAOMessage implements MqttStandardMessage {
    
    protected String uniqueCode;
    protected String deviceId;
    protected String signalId;
    protected String type;
    protected Double value;

    public SingleAOMessage(String uniqueCode, String deviceId, 
            String signalId, String type, Double value) {
        this.uniqueCode = uniqueCode;
        this.deviceId = deviceId;
        this.signalId = signalId;
        this.type = type;
        this.value = value;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AOSetMessage{" + "uniqueCode=" + uniqueCode + ", deviceId=" + deviceId + ", signalId=" + signalId + ", type=" + type + ", value=" + value + '}';
    }
    
}
