package com.kongtrolink.framework.gateway.common.entity.send;

import com.kongtrolink.framework.gateway.common.entity.send.info.BaseId;

import java.util.List;

/**
 * IAIoT Cloud向IAIoT Edge获取设备数据模型信息
 * Created by Mag on 2019/10/14.
 */
public class GetDeviceDataModel {

    private List<BaseId> devices;

    public List<BaseId> getDevices() {
        return devices;
    }

    public void setDevices(List<BaseId> devices) {
        this.devices = devices;
    }
}
