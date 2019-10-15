package com.kongtrolink.framework.mqtt.mqttEntity;

/**
 * @Auther: liudd
 * @Date: 2019/9/25 18:23
 * @Description:设备型号封装类
 */
public class DeviceTypeResult {

    private String deviceType;
    private String model;
    private String enterpriseCode;
    private String service;

    public DeviceTypeResult(String enterpriseCode, String service, String deviceType, String model) {
        this.deviceType = deviceType;
        this.model = model;
        this.enterpriseCode = enterpriseCode;
        this.service = service;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
