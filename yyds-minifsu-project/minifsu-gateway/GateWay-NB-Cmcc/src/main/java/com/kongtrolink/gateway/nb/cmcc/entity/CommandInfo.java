package com.kongtrolink.gateway.nb.cmcc.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/2/15.
 */
public class CommandInfo implements Serializable {
    private static final long serialVersionUID = -6824929568428011219L;

    private String command;
    private String imei;
    private Integer objId;
    private Integer objInstId;
    private Integer executeResId;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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
