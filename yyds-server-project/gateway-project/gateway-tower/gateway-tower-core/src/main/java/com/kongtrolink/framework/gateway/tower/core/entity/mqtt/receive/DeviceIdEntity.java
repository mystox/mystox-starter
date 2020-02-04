package com.kongtrolink.framework.gateway.tower.core.entity.mqtt.receive;

import java.util.List;

public class DeviceIdEntity {


    private List<DeviceIdInfo> deviceIds;

    public List<DeviceIdInfo> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<DeviceIdInfo> deviceIds) {
        this.deviceIds = deviceIds;
    }
}
