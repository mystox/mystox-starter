package com.kongtrolink.framework.gateway.tower.entity.rec.info;

import java.util.List;

/**
 * IAIoT Edge向IAIoT Cloud推送设备资产信息
 * 设备信息
 * Created by Mag on 2019/10/14.
 */
public class PushDeviceAssetDeviceList {

    private List<PushDeviceAssetDevice> devices;//设备信息

    public List<PushDeviceAssetDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<PushDeviceAssetDevice> devices) {
        this.devices = devices;
    }
}
