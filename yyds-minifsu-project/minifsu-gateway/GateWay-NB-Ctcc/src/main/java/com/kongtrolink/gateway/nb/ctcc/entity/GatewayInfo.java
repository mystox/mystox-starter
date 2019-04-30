package com.kongtrolink.gateway.nb.ctcc.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2018/11/27.
 */
public class GatewayInfo implements Serializable{

    private static final long serialVersionUID = 5072284441596754091L;

    private String deviceId;//电信平台生成的唯一ID
    private String deviceType;//设备类型
    private String serviceId; //profile 里面的  ## ServiceId
    private String method; //profile 里面的

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
