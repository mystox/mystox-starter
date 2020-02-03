package com.kongtrolink.framework.gateway.iaiot.core.rec.info;


import java.util.List;

/**
 * 设置告警参数 回复设备
 * Created by Mag on 2019/10/14.
 */
public class SetAlarmParamAckDeviceList {

    private List<SetAlarmParamAckDevice> devices;

    public List<SetAlarmParamAckDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<SetAlarmParamAckDevice> devices) {
        this.devices = devices;
    }
}
