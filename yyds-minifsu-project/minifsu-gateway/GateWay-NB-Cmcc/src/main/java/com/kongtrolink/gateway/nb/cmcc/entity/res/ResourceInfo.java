package com.kongtrolink.gateway.nb.cmcc.entity.res;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/2/19.
 */
public class ResourceInfo implements Serializable {

    private static final long serialVersionUID = 1280879206536592260L;

    private String opid;
    private int gatewayType;
    private int gatewayId;
    private int gatewaySubId;
    private ResourcePdu pdu;

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
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

    public int getGatewaySubId() {
        return gatewaySubId;
    }

    public void setGatewaySubId(int gatewaySubId) {
        this.gatewaySubId = gatewaySubId;
    }

    public ResourcePdu getPdu() {
        return pdu;
    }

    public void setPdu(ResourcePdu pdu) {
        this.pdu = pdu;
    }
}
