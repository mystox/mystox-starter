package com.kongtrolink.framework.register.entity;

/**
 * Created by mystoxlol on 2019/9/30, 8:31.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ServerMsg {
    private String host;
    private int port;
    private String serverName;
    private String serverVersion;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }
}
