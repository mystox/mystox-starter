package com.kongtrolink.framework.gateway.tower.server.entity;

/**
 * Created by Mag on 2019/10/17.
 */
public class MessageEntity {

    private String sn;
    private String msgType;
    private String payload;

    public MessageEntity() {
    }

    public MessageEntity(String sn, String msgType, String payload) {
        this.sn = sn;
        this.msgType = msgType;
        this.payload = payload;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
