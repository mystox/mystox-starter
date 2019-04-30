package com.kongtrolink.gateway.nb.ctcc.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/2/26.
 */
public class PduEntity implements Serializable {
    private static final long serialVersionUID = -7316547387057258109L;

    private String msgid;

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}
