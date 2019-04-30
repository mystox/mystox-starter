package com.kongtrolink.gateway.tcp.server.entity;

import java.io.Serializable;

/**
 * 注销报文
 * by Mag on 2019/3/27.
 */
public class NorthClearUpMessage implements Serializable {

    private static final long serialVersionUID = 8391093453082782325L;

    private String pktType;
    private String uuid;
    private NorthClearUpData payload;

    public NorthClearUpMessage() {
    }

    public NorthClearUpMessage(String uuid,String serverHost, String serverName) {
        this.pktType = "cleanUp";
        this.uuid = uuid;
        NorthClearUpData payload = new NorthClearUpData(serverHost,serverName);
        this.payload = payload;
    }

    public String getPktType() {
        return pktType;
    }

    public void setPktType(String pktType) {
        this.pktType = pktType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public NorthClearUpData getPayload() {
        return payload;
    }

    public void setPayload(NorthClearUpData payload) {
        this.payload = payload;
    }
}
