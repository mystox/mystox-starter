package com.kongtrolink.gateway.nb.cmcc.entity.iot;


import com.kongtrolink.gateway.nb.cmcc.util.EntityUtil;
import com.kongtrolink.gateway.nb.cmcc.util.Util;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/2/15.
 */
public class DataPushAck implements Serializable {
    private static final long serialVersionUID = 6001797945339304803L;

    private DataPushMsg msg;
    private String msg_signature; //消息摘要
    private String nonce;//用于计算消息摘要的随机串

    public void initDataPush(Util.BodyObj obj){
        this.nonce = obj.getNonce();
        this.msg_signature = obj.getMsg_signature();
        this.msg = EntityUtil.getEntityObject(obj.getMsg(),DataPushMsg.class);
    }
    public DataPushMsg getMsg() {
        return msg;
    }

    public void setMsg(DataPushMsg msg) {
        this.msg = msg;
    }

    public String getMsg_signature() {
        return msg_signature;
    }

    public void setMsg_signature(String msg_signature) {
        this.msg_signature = msg_signature;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    @Override
    public String toString() {
        return "DataPushAck{" +
                "msg=" + msg.toString() + "\n" +
                ", msg_signature='" + msg_signature + '\'' +
                ", nonce='" + nonce + '\'' +
                '}';
    }
}
