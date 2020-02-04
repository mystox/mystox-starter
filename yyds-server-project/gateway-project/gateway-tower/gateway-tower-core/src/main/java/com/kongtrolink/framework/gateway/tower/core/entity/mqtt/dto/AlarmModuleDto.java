package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.dto;

import com.kongtrolink.framework.gateway.tower.core.entity.msg.SendAlarm;

import java.io.Serializable;

public class AlarmModuleDto implements Serializable {

    private static final long serialVersionUID = -3034266782975929305L;
    private String uniqueCode;
    private SendAlarm sendAlarm;

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public SendAlarm getSendAlarm() {
        return sendAlarm;
    }

    public void setSendAlarm(SendAlarm sendAlarm) {
        this.sendAlarm = sendAlarm;
    }
}
