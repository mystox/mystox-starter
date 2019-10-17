package com.kongtrolink.framework.gateway.common.entity.rec.info;

/**
 * 设备信息
 * Created by Mag on 2019/10/14.
 */
public class PushDeviceAssetDevice {

    private String id;//设备ID
    private String name	;//	设备名称
    private int type;//	设备类型
    private String model;//设备型号
    private int resourceNo;//设备资源编号
    private int versionMajor;//设备协议主版本号
    private int versionMinor;//	设备协议次版本号
    private int versionRevision;//设备协议修订版本号

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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
