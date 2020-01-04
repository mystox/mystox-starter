package com.kongtrolink.framework.gateway.tower.entity.send;

import com.kongtrolink.framework.gateway.tower.entity.send.info.BaseId;

import java.util.List;

/**
 * 设备信息
 * Created by Mag on 2019/10/14.
 */
public class GetDeviceAlarmModel {

    private List<BaseId> devices;

    public List<BaseId> getDevices() {
        return devices;
    }

    public void setDevices(List<BaseId> devices) {
        this.devices = devices;
    }
}