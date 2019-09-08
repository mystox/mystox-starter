package com.kongtrolink.framework.entity;

/**
 * Created by mystoxlol on 2019/9/5, 14:48.
 * company: kongtrolink
 * description:
 * update record:
 */
public class MqttResp {
    private String msgId;
    private String topic;
    private PayloadType payloadType;
    private String payload;
    private byte[] bytePayload;

    public MqttResp(String msgId, String payload) {
        this.msgId = msgId;
        this.topic = topic;
        this.payloadType = PayloadType.STRING;
        this.payload = payload;
        this.bytePayload = bytePayload;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public byte[] getBytePayload() {
        return bytePayload;
    }

    public void setBytePayload(byte[] bytePayload) {
        this.bytePayload = bytePayload;
    }
}
