package com.kongtrolink.framework.gateway.tower.entity.send;

import com.kongtrolink.framework.gateway.tower.entity.send.info.GetAlarmParamDevice;

import java.util.List;

/**
 * 获取告警参数
 * Created by Mag on 2019/10/14.
 */
public class GetAlarmParam {

    private List<GetAlarmParamDevice> devices;

    public List<GetAlarmParamDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<GetAlarmParamDevice> devices) {
        this.devices = devices;
    }
}
