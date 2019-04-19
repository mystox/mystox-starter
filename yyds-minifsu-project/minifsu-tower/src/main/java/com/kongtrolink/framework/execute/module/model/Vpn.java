package com.kongtrolink.framework.execute.module.model;

/**
 * @author fengw
 * Vpn信息
 * 新建文件 2019-4-17 13:57:51
 */
public class Vpn {
    //VPN名称
    private String vpnName;
    //VPN本地名称
    private String localName;
    //注册机IP
    private String loginIp;
    //注册机端口
    private int loginPort;

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public int getLoginPort() {
        return loginPort;
    }

    public void setLoginPort(int loginPort) {
        this.loginPort = loginPort;
    }
}
