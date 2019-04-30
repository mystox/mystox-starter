package com.kongtrolink.gateway.nb.ctcc.iot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * NBIot-平台的配置
 * by Mag on 2018/11/20.
 */
@Component
@ConfigurationProperties(prefix = "nbIot")
public class NbIotConfig {

    private String outIp;//本机IP 是对外的IP 接收电信推送用
    private String outPort;//本机端口 是对外的端口 接收电信推送用

    private String platformIp;//电信IOT平台 IP
    private String platformPort;//电信IOT平台 端口
    private String appId;//在电信上面注册的应用ID
    private String secret;//在电信上面注册的应用密钥
    private int packageTime;//分包超时时长 单位 秒



    public String getPlatformIp() {
        return platformIp;
    }

    public void setPlatformIp(String platformIp) {
        this.platformIp = platformIp;
    }

    public String getPlatformPort() {
        return platformPort;
    }

    public void setPlatformPort(String platformPort) {
        this.platformPort = platformPort;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getPackageTime() {
        return packageTime;
    }

    public void setPackageTime(int packageTime) {
        this.packageTime = packageTime;
    }

    public String getOutIp() {
        return outIp;
    }

    public void setOutIp(String outIp) {
        this.outIp = outIp;
    }

    public String getOutPort() {
        return outPort;
    }

    public void setOutPort(String outPort) {
        this.outPort = outPort;
    }
}
