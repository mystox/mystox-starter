package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.io.Serializable;
import java.util.List;

/**
 * SC请求获取门限值下发报文
 * Created by Eric on 2019/6/17.
 */
public class GetThresholdAckMessage implements Serializable{

    private static final long serialVersionUID = -7001078305253047007L;
    private String msgId;
    private String fsuId;
    private int result; // //1 正确 0 错误
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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
