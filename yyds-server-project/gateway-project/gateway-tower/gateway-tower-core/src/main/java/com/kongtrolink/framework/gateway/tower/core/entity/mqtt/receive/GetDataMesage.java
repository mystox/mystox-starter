package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.io.Serializable;

/**
 * SC请求获取监控点数据下发报文
 * Created by Eric on 2019/6/14.
 */
public class GetDataMesage implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -2602934675026013808L;

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
