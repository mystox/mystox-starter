package com.kongtrolink.framework.gateway.common.entity.rec.base;

import java.io.Serializable;

/**
 * 接收消息
 * Created by Mag on 2019/10/14.
 */
public class RecBase implements Serializable{

    private String msgId;
    private String moduleId;

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
}
