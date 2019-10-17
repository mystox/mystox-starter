package com.kongtrolink.framework.gateway.tower.entity.send.info;

/**
 * 设备信息
 * Created by Mag on 2019/10/14.
 */
public class GetDeviceDataModelDevice {

    private int type;
    private int resourceNo;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(int resourceNo) {
        this.resourceNo = resourceNo;
    }
}
