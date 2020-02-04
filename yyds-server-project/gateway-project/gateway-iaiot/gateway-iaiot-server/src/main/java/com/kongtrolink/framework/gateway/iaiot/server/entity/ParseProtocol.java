package com.kongtrolink.framework.gateway.iaiot.server.entity;

/**
 * Created by mystoxlol on 2019/10/16, 14:30.
 * company: kongtrolink
 * description: 非标准协议
 * update record:
 */
public class ParseProtocol {
    String msgType;
    String sn;
    String uuid;
    String payload;

    public ParseProtocol() {
    }

    public ParseProtocol(String msgType, String sn, String uuid, String payload) {
        this.msgType = msgType;
        this.sn = sn;
        this.uuid = uuid;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "ParseProtocol{" +
                "msgType='" + msgType + '\'' +
                ", sn='" + sn + '\'' +
                ", uuid='" + uuid + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
