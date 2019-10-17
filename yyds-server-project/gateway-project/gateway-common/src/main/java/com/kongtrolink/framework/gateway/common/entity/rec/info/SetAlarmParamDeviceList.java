package com.kongtrolink.framework.gateway.common.entity.rec.info;


import java.util.List;

/**
 * 设置告警参数 回复设备
 * Created by Mag on 2019/10/14.
 */
public class SetAlarmParamDeviceList {

    private List<SetAlarmParamDevice> devices;

    public List<SetAlarmParamDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<SetAlarmParamDevice> devices) {
        this.devices = devices;
    }
}
