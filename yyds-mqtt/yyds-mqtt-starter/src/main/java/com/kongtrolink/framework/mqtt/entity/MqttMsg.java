package com.kongtrolink.framework.mqtt.entity;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by mystoxlol on 2019/8/13, 10:35.
 * company: kongtrolink
 * description:
 * update record:
 */
public class MqttMsg {
    private String msgId = UUID.randomUUID().toString();
    private String topic;
    private PayloadType payloadType;
    private String payload;
    private byte[] bytePayload;

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

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    public byte[] getBytePayload() {
        return bytePayload;
    }

    public void setBytePayload(byte[] bytePayload) {
        this.bytePayload = bytePayload;
    }

    @Override
    public String toString() {
        return "MqttMsg{" +
                "msgId='" + msgId + '\'' +
                ", topic='" + topic + '\'' +
                ", payloadType=" + payloadType +
                ", payload='" + payload + '\'' +
                ", bytePayload=" + Arrays.toString(bytePayload) +
                '}';
    }
}
