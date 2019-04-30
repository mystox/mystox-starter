package com.kongtrolink.gateway.nb.cmcc.iot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * NBIot-平台的配置
 * by Mag on 2018/11/20.
 */
@Component
@ConfigurationProperties(prefix = "nbIot")
public class NbIotConfig {

    private String token;//本机IP 是对外的IP 接收电信推送用
    private String masterApiKey;//在电信上面注册的应用ID
    private int packageTime;//分包超时时长 单位 秒

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMasterApiKey() {
        return masterApiKey;
    }

    public void setMasterApiKey(String masterApiKey) {
        this.masterApiKey = masterApiKey;
    }

    public int getPackageTime() {
        return packageTime;
    }

    public void setPackageTime(int packageTime) {
        this.packageTime = packageTime;
    }

}
