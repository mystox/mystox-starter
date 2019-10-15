package com.kongtrolink.framework.mqtt.mqttEntity;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/25 18:23
 * @Description:获取设备信号mqtt返回结果封装类
 */
public class DeviceTypeMqttResult {

    private String enterpriseCode;
    private String service;
    private List<DeviceTypeResult> deviceTypeResultList;

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

    public List<DeviceTypeResult> getDeviceTypeResultList() {
        return deviceTypeResultList;
    }

    public void setDeviceTypeResultList(List<DeviceTypeResult> deviceTypeResultList) {
        this.deviceTypeResultList = deviceTypeResultList;
    }
}
