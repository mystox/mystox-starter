package com.kongtrolink.framework.scloud.entity.multRoom;

import com.kongtrolink.framework.scloud.entity.DeviceEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 设备列表
 * by Mag on 6/21/2018.
 */
public class RoomDeviceModel implements Serializable{


    private static final long serialVersionUID = -1753972266939138425L;
    private String type;
    private List<DeviceEntity> infoList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DeviceEntity> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<DeviceEntity> infoList) {
        this.infoList = infoList;
    }
}
