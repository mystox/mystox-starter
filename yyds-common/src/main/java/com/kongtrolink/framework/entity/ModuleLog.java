package com.kongtrolink.framework.entity;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/9/19, 10:00.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ModuleLog {
    private String id;
    private Date time; //产生日志时间
    private Integer stateCode; //错误日志类型与错误状态一致
    private String operaCode; //操作码
    private String msgId; //消息id
    private String sourceAddress; //消息源

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getStateCode() {
        return stateCode;
    }

    public void setStateCode(Integer stateCode) {
        this.stateCode = stateCode;
    }

    public String getOperaCode() {
        return operaCode;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
}
