package com.kongtrolink.framework.gateway.iaiot.core.rec.info;

import java.util.List;

/**
 * 告警点信息
 * Created by Mag on 2019/10/14.
 */
public class AlarmDeviceList {

    private List<AlarmDevice> devices;

    public List<AlarmDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<AlarmDevice> devices) {
        this.devices = devices;
    }
}
