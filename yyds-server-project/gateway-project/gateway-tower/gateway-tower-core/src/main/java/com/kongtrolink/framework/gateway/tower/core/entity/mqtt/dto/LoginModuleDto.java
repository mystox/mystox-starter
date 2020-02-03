package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.dto;

import com.kongtrolink.framework.gateway.tower.core.entity.msg.Login;

import java.io.Serializable;

public class LoginModuleDto implements Serializable {

    private static final long serialVersionUID = -4810622574058315628L;

    private Login login;
    private String fsuId;

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }
}
