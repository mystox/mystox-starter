package com.kongtrolink.framework.gateway.tower.entity.send;

import com.kongtrolink.framework.gateway.tower.entity.rec.info.AlarmDevice;

import java.util.List;

/**
 * 设置告警参数
 * Created by Mag on 2019/10/14.
 */
public class SetAlarmParam {

    private List<AlarmDevice> devices;

    public List<AlarmDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<AlarmDevice> devices) {
        this.devices = devices;
    }
}
