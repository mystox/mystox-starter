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
 * 基于设备的 MQTT 通信报文
 * 
 * @author Mosaico
 */
public class DeviceBasedMessage implements MqttStandardMessage {
    
    protected String uniqueCode;
    protected String deviceId;

    public DeviceBasedMessage(String uniqueCode, String deviceId) {
        this.uniqueCode = uniqueCode;
        this.deviceId = deviceId;
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

    @Override
    public String toString() {
        return "SignalUpdateMessage{" + "uniqueCode=" + uniqueCode + ", deviceId=" + deviceId + '}';
    }
    
}
