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

import com.kongtrolink.framework.core.entity.MqttStandardMessage;

/**
 * 设置单点遥信信号报文
 * 
 * @author Mosaico
 */
public class SingleDIMessage implements MqttStandardMessage {
    
    protected String uniqueCode;
    protected String deviceId;
    protected String signalId;
    protected Double threshold;

    public SingleDIMessage(String uniqueCode, String deviceId, 
            String signalId, Double threshold) {
        this.uniqueCode = uniqueCode;
        this.deviceId = deviceId;
        this.signalId = signalId;
        this.threshold = threshold;
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

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "DIUpdateMessage{" + "uniqueCode=" + uniqueCode + ", deviceId=" + deviceId + ", signalId=" + signalId + ", threshold=" + threshold + '}';
    }
    
}
