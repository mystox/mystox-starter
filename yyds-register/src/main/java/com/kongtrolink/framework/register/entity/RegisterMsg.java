package com.kongtrolink.framework.register.entity;

/**
 * Created by mystoxlol on 2019/8/28, 14:51.
 * company: kongtrolink
 * description:
 * update record:
 */
public class RegisterMsg {
    private String registerUrl;
    private RegisterType registerType;


    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public RegisterType getRegisterType() {
        return registerType;
    }

    public void setRegisterType(RegisterType registerType) {
        this.registerType = registerType;
    }
}
