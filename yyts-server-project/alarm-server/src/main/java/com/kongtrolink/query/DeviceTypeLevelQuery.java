package com.kongtrolink.query;

import com.kongtrolink.base.Paging;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 16:40
 * @Description:
 */
public class DeviceTypeLevelQuery extends Paging {

    private String id;
    private String uniqueCode;
    private String service;
    private String deviceName;
    private String deviceType;
    private String level;

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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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
