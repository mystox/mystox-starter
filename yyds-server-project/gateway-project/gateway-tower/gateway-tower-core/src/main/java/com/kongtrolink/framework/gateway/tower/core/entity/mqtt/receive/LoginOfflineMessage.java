package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.io.Serializable;

public class LoginOfflineMessage implements Serializable {

    private static final long serialVersionUID = 4699165327076713256L;

    private String fsuId;
    private String enterpriseCode;
    private String serverCode;
    private String gatewayServerCode;

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
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
}
