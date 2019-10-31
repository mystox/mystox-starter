package com.kongtrolink.framework.mqtt;

/**
 * @Auther: liudd
 * @Date: 2019/10/16 09:29
 * @Description:
 */
public class DeviceTypeResult {

    private String deviceType;
    private String model;
    private String enterpriseCode;
    private String serverCode;

    public DeviceTypeResult(String enterpriseCode, String serverCode, String deviceType, String model) {
        this.deviceType = deviceType;
        this.model = model;
        this.enterpriseCode = enterpriseCode;
        this.serverCode = serverCode;
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

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }
}
