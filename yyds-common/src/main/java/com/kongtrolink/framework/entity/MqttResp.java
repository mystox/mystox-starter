package com.kongtrolink.framework.entity;

import java.util.Arrays;

/**
 * Created by mystoxlol on 2019/9/5, 14:48.
 * company: kongtrolink
 * description:
 * update record:
 */
public class MqttResp {
    private String msgId = "1"+System.currentTimeMillis();
    private String topic;
    private PayloadType payloadType;
    private String payload;
    private byte[] bytePayload;
    private Integer stateCode = StateCode.SUCCESS;
    private boolean subpackage = false;
    private Integer packageCount;//分包总数
    private Integer packageNum;//分包编号
    private Integer crc;//总包校验

    public MqttResp(String msgId, byte[] bytePayload, boolean subpackage, Integer packageNum,Integer packageCount, Integer crc) {
        this.msgId = msgId;
        this.topic = topic;
        this.payloadType = PayloadType.BYTE;
        this.bytePayload = bytePayload;
        this.packageNum = packageNum;
        this.subpackage = subpackage;
        this.packageCount = packageCount;
        this.crc = crc;
    }

    public MqttResp(String msgId, byte[] bytePayload) {
        this.msgId = msgId;
        this.topic = topic;
        this.payloadType = PayloadType.BYTE;
        this.bytePayload = bytePayload;
    }

    public MqttResp(String msgId, String payload) {
        this.msgId = msgId;
        this.topic = topic;
        this.payloadType = PayloadType.STRING;
        this.payload = payload;
        this.bytePayload = bytePayload;
    }

    public MqttResp() {
    }

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
    }

    public boolean isSubpackage() {
        return subpackage;
    }

    public void setSubpackage(boolean subpackage) {
        this.subpackage = subpackage;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public Integer getCrc() {
        return crc;
    }

    public void setCrc(Integer crc) {
        this.crc = crc;
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

    @Override
    public String toString() {
        return "MqttResp{" +
                "msgId='" + msgId + '\'' +
                ", topic='" + topic + '\'' +
                ", payloadType=" + payloadType +
                ", payload='" + payload + '\'' +
                ", bytePayload=" + Arrays.toString(bytePayload) +
                ", stateCode=" + stateCode +
                '}';
    }
}
