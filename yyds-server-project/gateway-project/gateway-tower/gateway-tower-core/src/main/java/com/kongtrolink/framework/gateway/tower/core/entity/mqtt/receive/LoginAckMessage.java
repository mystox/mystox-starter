package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.io.Serializable;

public class LoginAckMessage implements Serializable {

    private static final long serialVersionUID = 3746944721236238894L;
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
