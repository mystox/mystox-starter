package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;
import java.util.List;

/**
 * 设备列表
 * by Mag on 6/21/2018.
 */
public class RoomDevice implements Serializable{

    private static final long serialVersionUID = -7630314095338294780L;
    private String type;
    private String code;
    private List<RoomDeviceInfo> infoList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<RoomDeviceInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<RoomDeviceInfo> infoList) {
        this.infoList = infoList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
