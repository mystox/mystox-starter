package com.kongtrolink.framework.gateway.iaiot.core.assent;

/**
 * 设备信息
 * Created by Mag on 2019/10/14.
 */
public class DeviceReportExtendInfo extends DeviceReportExtend{


    private static final long serialVersionUID = -257882584618486806L;

    private String name	;//	设备名称
    private int resourceNo;//设备资源编号
    private int versionMajor;//设备协议主版本号
    private int versionMinor;//	设备协议次版本号
    private int versionRevision;//设备协议修订版本号


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(int resourceNo) {
        this.resourceNo = resourceNo;
    }

    public int getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor(int versionMajor) {
        this.versionMajor = versionMajor;
    }

    public int getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor(int versionMinor) {
        this.versionMinor = versionMinor;
    }

    public int getVersionRevision() {
        return versionRevision;
    }

    public void setVersionRevision(int versionRevision) {
        this.versionRevision = versionRevision;
    }
}
