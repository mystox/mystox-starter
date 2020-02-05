package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.io.Serializable;

/**
 * SC请求获取监控点数据下发报文
 * Created by Eric on 2019/6/14.
 */
public class GetDataMessage implements Serializable{
    private static final long serialVersionUID = -4471874791195635080L;

    private String msgId;
    private String fsuId;
    private DeviceIdEntity payload;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public DeviceIdEntity getPayload() {
        return payload;
    }

    public void setPayload(DeviceIdEntity payload) {
        this.payload = payload;
    }
}
