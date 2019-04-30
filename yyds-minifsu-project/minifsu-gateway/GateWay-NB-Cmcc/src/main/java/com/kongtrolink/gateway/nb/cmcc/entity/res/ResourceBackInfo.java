package com.kongtrolink.gateway.nb.cmcc.entity.res;


import com.kongtrolink.gateway.nb.cmcc.entity.iot.ResourceAck;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/2/19.
 */
public class ResourceBackInfo implements Serializable {

    private static final long serialVersionUID = 1280879206536592260L;

    private String opid;
    private int gatewayType;
    private int gatewayId;
    private int gatewaySubId;
    private ResourceBackPdu pdu;

    public void initBack(ResourceInfo info,ResourceAck ack){
        this.opid = info.getOpid();
        this.gatewayType = info.getGatewayType();
        this.gatewayId = info.getGatewayId();
        this.gatewaySubId = info.getGatewaySubId();
        ResourcePdu pduInfo = info.getPdu();
        if(pduInfo !=null){
            ResourceBackPdu pdu = new ResourceBackPdu();
            pdu.initPdu(pduInfo);
            ResourceJson json = pduInfo.getJson();
            if(json !=null){
                ResourceBackJson backJson = new ResourceBackJson();
                backJson.initBack(ack,json);
                pdu.setJson(backJson);
            }
            this.pdu = pdu;
        }
    }
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

    public ResourceBackPdu getPdu() {
        return pdu;
    }

    public void setPdu(ResourceBackPdu pdu) {
        this.pdu = pdu;
    }
}
