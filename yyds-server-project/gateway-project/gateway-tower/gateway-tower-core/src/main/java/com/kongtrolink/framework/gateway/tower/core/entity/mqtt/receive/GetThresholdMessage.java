package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.io.Serializable;

/**
 * SC请求获取门限值下发报文
 * Created by Eric on 2019/6/17.
 */
public class GetThresholdMessage implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -5396756026134714301L;

    private String reqId;
    private String uniqueCode;
    private String deviceId;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
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
}
