package com.kongtrolink.framework.core.entity;

import java.io.Serializable;

/**
 * 向上发送数据
 * 报文请求
 {"pktType":"connect",
 "uuid":"uuid",
 "GIP":"网关ip",
 "payload":{
 }}
 * by Mag on 2019/3/27.
 */
public class NorthMessage implements Serializable {
    private static final long serialVersionUID = -375079311812607921L;

    private String pktType = "connect";
    private String uuid;
    private String gip;
    private String payload;

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

    public String getGip() {
        return gip;
    }

    public void setGip(String gip) {
        this.gip = gip;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
