package com.kongtrolink.framework.gateway.iaiot.core.rec.info;

import java.util.List;

/**
 * 设置数据点数据 返回
 * Created by Mag on 2019/10/14.
 */
public class SetDataAckInfo {

    private List<DataDeviceResultInfo> devices;//		array	是	数据点列表

    public List<DataDeviceResultInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<DataDeviceResultInfo> devices) {
        this.devices = devices;
    }
}
