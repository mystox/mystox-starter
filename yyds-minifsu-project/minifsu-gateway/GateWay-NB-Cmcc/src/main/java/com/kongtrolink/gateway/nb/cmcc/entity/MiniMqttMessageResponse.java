package com.kongtrolink.gateway.nb.cmcc.entity;

import java.io.Serializable;

/**
 *  接收到 适配平台(OMC) 的 MQTT 命令
 *  by Mag on 2018/10/17.
 */
public class MiniMqttMessageResponse implements Serializable {

    private static final long serialVersionUID = -3399209682358768380L;

    private String opid; //操作码， 现在NBIOT没不支持，填“”，GSM TCP可以回传
    private String imei;
    private Integer objId;
    private Integer objInstId;
    private Integer executeResId;

    private String pdu; //2.3的MINIFSU业务JSON消息

    public String getOpid() {
        return opid;
    }

    public void setOpid(String opid) {
        this.opid = opid;
    }

    public String getPdu() {
        return pdu;
    }

    public void setPdu(String pdu) {
        this.pdu = pdu;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Integer getObjId() {
        return objId;
    }

    public void setObjId(Integer objId) {
        this.objId = objId;
    }

    public Integer getObjInstId() {
        return objInstId;
    }

    public void setObjInstId(Integer objInstId) {
        this.objInstId = objInstId;
    }

    public Integer getExecuteResId() {
        return executeResId;
    }

    public void setExecuteResId(Integer executeResId) {
        this.executeResId = executeResId;
    }
}
