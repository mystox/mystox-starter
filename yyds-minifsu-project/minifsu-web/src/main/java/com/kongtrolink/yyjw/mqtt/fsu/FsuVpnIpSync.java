package com.kongtrolink.yyjw.mqtt.fsu;

import java.io.Serializable;

/**
 * Created by John on 2017/10/20.
 * FSU的 VPN IP
 */
public class FsuVpnIpSync implements Serializable{

    private static final long serialVersionUID = 2121021508225373414L;

    private String fsuCode;
    private String ip;
    private String httpPort;
    private String onvifPort;

    public FsuVpnIpSync() {
    }

    public FsuVpnIpSync(String fsuCode, String ip, String httpPort, String onvifPort) {
        this.fsuCode = fsuCode;
        this.ip = ip;
        this.httpPort = httpPort;
        this.onvifPort = onvifPort;
    }

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    public String getOnvifPort() {
        return onvifPort;
    }

    public void setOnvifPort(String onvifPort) {
        this.onvifPort = onvifPort;
    }
}
