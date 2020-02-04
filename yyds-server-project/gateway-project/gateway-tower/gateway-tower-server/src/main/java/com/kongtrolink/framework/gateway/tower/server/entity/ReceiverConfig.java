package com.kongtrolink.framework.gateway.tower.server.entity;

/**
 * Created by mystoxlol on 2019/10/16, 11:04.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ReceiverConfig {

    private String name;
    private String businessCode;
    private String deviceType;
    private String deviceModel;
    private String regionCode;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
}
