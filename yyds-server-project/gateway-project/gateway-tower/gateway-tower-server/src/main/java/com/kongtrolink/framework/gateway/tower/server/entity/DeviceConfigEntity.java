package com.kongtrolink.framework.gateway.tower.server.entity;

import java.io.Serializable;

public class DeviceConfigEntity implements Serializable {
    private static final long serialVersionUID = -6475677508968988252L;

    private String deviceName;
    private String reportType;
    private String assentType ;
    private int isRoot ;

    public DeviceConfigEntity() {
    }

    public DeviceConfigEntity(String deviceName, String reportType, String assentType, int isRoot) {
        this.deviceName = deviceName;
        this.reportType = reportType;
        this.assentType = assentType;
        this.isRoot = isRoot;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getAssentType() {
        return assentType;
    }

    public void setAssentType(String assentType) {
        this.assentType = assentType;
    }

    public int getIsRoot() {
        return isRoot;
    }

    public void setIsRoot(int isRoot) {
        this.isRoot = isRoot;
    }
}
