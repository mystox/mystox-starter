package com.kongtrolink.framework.scloud.entity.realtime;

import java.io.Serializable;

/**
 * 遥测信号点统计表
 * Created by Mg on 2018/5/25.
 */
public class SignalDiInfo implements Serializable{

    private static final long serialVersionUID = -6086432407626334461L;

    private String tier;//站点层级

    private String siteName;//站点名称

    private String siteType;//站点类型

    private String siteCode; //站点编码

    private String deviceName; //设备名称

    private String deviceCode; //设备编码

    private String value; //遥测信号值

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
