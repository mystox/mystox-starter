package com.kongtrolink.framework.gateway.tower.entity.rec.info;

import java.util.List;

/**
 * 告警点信息
 * Created by Mag on 2019/10/14.
 */
public class AlarmDeviceModelList {


    private List<AlarmDeviceModel> devices;

    public List<AlarmDeviceModel> getDevices() {
        return devices;
    }

    public void setDevices(List<AlarmDeviceModel> devices) {
        this.devices = devices;
    }
}
