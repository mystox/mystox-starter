package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.util.ArrayList;
import java.util.List;

public class DeviceIdInfo {

    private String deviceId;  //ID
    private List<SignalIdInfo> ids; //信号点类型

    public DeviceIdInfo() {
    }

    public DeviceIdInfo(String deviceId) {
        this.deviceId = deviceId;
        this.ids = new ArrayList<>();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<SignalIdInfo> getIds() {
        return ids;
    }

    public void setIds(List<SignalIdInfo> ids) {
        this.ids = ids;
    }
}
