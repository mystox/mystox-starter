package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.back;

import java.io.Serializable;
import java.lang.ref.PhantomReference;

/**
 * 返回,响应SC报文 消息体
 */
public class SendScMessage implements Serializable {

    private static final long serialVersionUID = 2624557239263517298L;

    private String msgId;//消息ID
    private String fsuId;//FSUid
    private int result;  //1 正确 0 错误
    private Object payload; //具体返回值

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
