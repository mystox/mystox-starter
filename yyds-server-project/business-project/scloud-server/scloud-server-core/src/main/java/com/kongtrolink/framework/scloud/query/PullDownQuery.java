package com.kongtrolink.framework.scloud.query;

import java.io.Serializable;

public class PullDownQuery implements Serializable {
    private static final long serialVersionUID = 8263823992492953766L;
    private String deviceType;
    private String  type;
    private String siteId;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }
}
