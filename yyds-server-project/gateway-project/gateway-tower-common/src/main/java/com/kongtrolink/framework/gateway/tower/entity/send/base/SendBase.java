package com.kongtrolink.framework.gateway.tower.entity.send.base;

import java.io.Serializable;

/**
 * 发送消息
 * Created by Mag on 2019/10/14.
 */
public class SendBase implements Serializable{

    private String msgId;
    private String moduleId;
    private Object payload;

    public SendBase() {
    }

    public SendBase(String msgId, Object payload) {
        this.msgId = msgId;
        this.payload = payload;
    }

    public SendBase(Object payload) {
        this.payload = payload;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
