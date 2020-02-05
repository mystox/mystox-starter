package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.io.Serializable;
import java.util.List;

/**
 * SC请求设置门限值下发报文
 * Created by Eric on 2019/6/17.
 */
public class SetThresholdAckMessage implements Serializable {
    private static final long serialVersionUID = -6999313684298687333L;
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
