/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.yyjw.mqtt.message;

/**
 *
 * @author Mosaico
 */
public class FsuPollingIntervalMessage implements MqttStandardMessage {
    
    private String uniqueCode;
    private int pollingInterval;

    public FsuPollingIntervalMessage() {
    }

    public FsuPollingIntervalMessage(String uniqueCode, int pollingInterval) {
        this.uniqueCode = uniqueCode;
        this.pollingInterval = pollingInterval;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    @Override
    public String toString() {
        return "FsuPollingIntervalMessage{" + "uniqueCode=" + uniqueCode + ", pollingInterval=" + pollingInterval + '}';
    }
    
}
