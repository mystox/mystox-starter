package com.kongtrolink.framework.core.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by mystoxlol on 2019/3/27, 16:35.
 * company: kongtrolink
 * description: 服务间通讯实体
 * update record:
 */
public class ModuleMsg {
    private String msgId = "1" + System.currentTimeMillis() / 1000;
    private String pktType; //消息通讯类型 类型参考PktType.class
    private String uuid;
    private String SN;
    private JSONObject payload;
    private JSONArray arrayPayload;

    public ModuleMsg() {
    }

    public ModuleMsg(String pktType, String uuid, String SN, JSONObject payload) {
        this.pktType = pktType;
        this.uuid = uuid;
        this.SN = SN;
        this.payload = payload;
    }

    public ModuleMsg(String pktType, String SN, JSONObject payload) {
        this.pktType = pktType;
        this.SN = SN;
        this.payload = payload;
    }

    public ModuleMsg(String pktType, JSONObject payload) {
        this.pktType = pktType;
        this.payload = payload;
    }

    public ModuleMsg(String pktType, String SN) {
        this.pktType = pktType;
        this.SN = SN;
    }

    public ModuleMsg(String pktType) {
        this.pktType = pktType;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

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

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }

    public JSONArray getArrayPayload() {
        return arrayPayload;
    }

    public void setArrayPayload(JSONArray arrayPayload) {
        this.arrayPayload = arrayPayload;
    }

    @Override
    public String toString() {
        return "ModuleMsg{" +
                "msgId='" + msgId + '\'' +
                ", pktType='" + pktType + '\'' +
                ", uuid='" + uuid + '\'' +
                ", SN='" + SN + '\'' +
                ", payload=" + payload +
                ", arrayPayload=" + arrayPayload +
                '}';
    }

}
