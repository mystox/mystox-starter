package com.kongtrolink.framework.gateway.tower.entity.rec.info;

import java.util.List;

/**
 * 设备数据模型信息
 * Created by Mag on 2019/10/14.
 */
public class DataDevicesInfo {

    private List<DataDeviceInfo>  devices;//		array	是	数据点列表

    public List<DataDeviceInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<DataDeviceInfo> devices) {
        this.devices = devices;
    }
}
