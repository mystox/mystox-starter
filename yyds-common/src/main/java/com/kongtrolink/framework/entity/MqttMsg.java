package com.kongtrolink.framework.entity;

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
    private String operaCode;
    private String sourceAddress; //消息源地址，一般为生产消息的服务code serverName+"_"+serverVersion
    private PayloadType payloadType;
    private String payload;
    private byte[] bytePayload;


    public String getOperaCode() {
        return operaCode;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
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
