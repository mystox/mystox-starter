package com.kongtrolink.gateway.nb.ctcc.entity;


import java.io.Serializable;

/**
 *  接收到 适配平台(OMC) 的 MQTT 命令
 *  by Mag on 2018/10/17.
 */
public class MiniMqttMessageResponse implements Serializable {

    private static final long serialVersionUID = -3399209682358768380L;

    private String opid; //操作码， 现在NBIOT没不支持，填“”，GSM TCP可以回传

    private String deviceId;

    private String pdu; //2.3的MINIFSU业务JSON消息

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public String getPdu() {
        return pdu;
    }

    public void setPdu(String pdu) {
        this.pdu = pdu;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
