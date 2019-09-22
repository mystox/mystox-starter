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
    private int stateCode = StateCode.SUCCESS;
    private boolean subpackage = false;
    private int packageCount;//分包总数
    private int packageNum;//分包编号


    public MqttResp(String msgId, byte[] bytePayload, boolean subpackage, int packageNum,int packageCount) {
        this.msgId = msgId;
        this.topic = topic;
        this.payloadType = PayloadType.BYTE;
        this.bytePayload = bytePayload;
        this.packageNum = packageNum;
        this.subpackage = subpackage;
        this.packageCount = packageCount;
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

    public int getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public int getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(int packageNum) {
        this.packageNum = packageNum;
    }

    public boolean isSubpackage() {
        return subpackage;
    }

    public void setSubpackage(boolean subpackage) {
        this.subpackage = subpackage;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
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
