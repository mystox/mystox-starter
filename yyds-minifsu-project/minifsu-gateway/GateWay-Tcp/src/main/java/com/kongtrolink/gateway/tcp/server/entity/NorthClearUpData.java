package com.kongtrolink.gateway.tcp.server.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * xx
 * by Mag on 2019/3/27.
 */
public class NorthClearUpData implements Serializable {
    private static final long serialVersionUID = 7570250999650090296L;
    private int code;
    private long time;
    private String serverHost;
    private String serverName;

    public NorthClearUpData() {
    }

    public NorthClearUpData(String serverHost, String serverName) {
        this.code = 4;
        this.time = new Date().getTime();
        this.serverHost = serverHost;
        this.serverName = serverName;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
