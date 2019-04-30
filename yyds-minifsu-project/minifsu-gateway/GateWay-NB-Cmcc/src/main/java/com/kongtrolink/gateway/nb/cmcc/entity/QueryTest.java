package com.kongtrolink.gateway.nb.cmcc.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2018/11/23.
 */
public class QueryTest implements Serializable{

    private static final long serialVersionUID = 5812092611723845425L;

    private int type;
    private String deviceId;
    private String serverId;
    private String method;
    private String pdu;
    private String nodeId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPdu() {
        return pdu;
    }

    public void setPdu(String pdu) {
        this.pdu = pdu;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
