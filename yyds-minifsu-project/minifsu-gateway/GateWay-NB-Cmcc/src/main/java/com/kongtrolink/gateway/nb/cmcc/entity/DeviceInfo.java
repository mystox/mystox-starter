package com.kongtrolink.gateway.nb.cmcc.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/2/14.
 */
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = -5980816860238252540L;
    private String title;
    private String imei;
    private String imsi;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
}
