package com.kongtrolink.framework.gateway.common.entity.send;

import com.kongtrolink.framework.gateway.common.entity.rec.info.DataDeviceInfo;

import java.util.List;

/**
 * 设置数据点数据
 * Created by Mag on 2019/10/14.
 */
public class SetData {

    private List<DataDeviceInfo> devices;//		array	是	数据点列表

    public List<DataDeviceInfo> getDevices() {
        return devices;
    }

    public void setDevices(List<DataDeviceInfo> devices) {
        this.devices = devices;
    }
}
