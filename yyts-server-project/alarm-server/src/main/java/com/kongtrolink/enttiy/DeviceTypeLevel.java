package com.kongtrolink.enttiy;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:03
 * @Description:企业，服务，设备型号拥有告警等级数量
 */
public class DeviceTypeLevel {
    private String id;
    private String uniqueCode;
    private String service;
    private String deviceName;
    private String deviceType;
    private String level;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
