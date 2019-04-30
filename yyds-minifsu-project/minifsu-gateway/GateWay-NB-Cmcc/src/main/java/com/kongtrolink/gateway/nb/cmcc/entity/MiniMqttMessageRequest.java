package com.kongtrolink.gateway.nb.cmcc.entity;

import java.io.Serializable;

/**
 *  服务 向  适配平台(OMC) 下发MQTT 命令
 *  by Mag on 2018/10/17.
 */
public class MiniMqttMessageRequest implements Serializable {

    private static final long serialVersionUID = 8168049957601243414L;
    /**
     * 网关类型
     * 1 电信 2 移动
     */
    private int gatewayType = 2;

    private int gatewayId = 101;

    private int gatewaySubId = 2; //网关类型

    private String deviceId;

    private Object pdu; //2.3的MINIFSU业务JSON消息

    public MiniMqttMessageRequest(Object pdu,String imei) {
        this.deviceId = imei;
        this.pdu = pdu;
    }

    public MiniMqttMessageRequest() {
    }

    public int getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(int gatewayType) {
        this.gatewayType = gatewayType;
    }

    public int getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(int gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Object getPdu() {
        return pdu;
    }

    public void setPdu(String pdu) {
        this.pdu = pdu;
    }

    public void setPdu(Object pdu) {
        this.pdu = pdu;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getGatewaySubId() {
        return gatewaySubId;
    }

    public void setGatewaySubId(int gatewaySubId) {
        this.gatewaySubId = gatewaySubId;
    }
}
