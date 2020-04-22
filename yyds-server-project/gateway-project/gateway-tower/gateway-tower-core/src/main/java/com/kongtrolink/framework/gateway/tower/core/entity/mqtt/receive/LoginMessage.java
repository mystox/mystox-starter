package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.io.Serializable;

public class LoginMessage implements Serializable {
    private static final long serialVersionUID = -1091307218001851760L;
    private String fsuId;
    private String username;
    private String password;
    private String ip;
    private String enterpriseCode;
    private String serverCode;
    private String gatewayServerCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getGatewayServerCode() {
        return gatewayServerCode;
    }

    public void setGatewayServerCode(String gatewayServerCode) {
        this.gatewayServerCode = gatewayServerCode;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }
}
