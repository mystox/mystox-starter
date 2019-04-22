package com.kongtrolink.framework.execute.module.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/4/22, 8:51.
 * company: kongtrolink
 * description: 保存终端报文日志
 * update record:
 */
public class TerminalLog {
    private String id;
    private String sn;
    private Date recordTime;
    private Integer pktType;
    private JSONObject payload;
    private Integer payloadSize;


    public TerminalLog() {
    }

    public TerminalLog(String sn, Integer pktType, Date recordTime, JSONObject payload) {
        this.sn = sn;
        this.pktType = pktType;
        this.recordTime = recordTime;
        this.payload = payload;
        if (payload != null)
            this.payloadSize = payload.toString().getBytes().length;
        else
            this.payloadSize = 0;
    }

    public Integer getPktType() {
        return pktType;
    }

    public void setPktType(Integer pktType) {
        this.pktType = pktType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }

    public Integer getPayloadSize() {
        return payloadSize;
    }

    public void setPayloadSize(Integer payloadSize) {
        this.payloadSize = payloadSize;
    }
}
