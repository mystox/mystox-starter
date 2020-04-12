package com.kongtrolink.framework.scloud.entity.multRoom;


import com.kongtrolink.framework.scloud.entity.SiteEntity;

import java.io.Serializable;

/**
 * 基本信息
 * Created by Mg on 6/20/2018.
 */
public class RoomSiteInfo extends SiteEntity {

    private static final long serialVersionUID = 5210704695095804208L;



    private int deviceNum;//监控设备总数

    private int signalNum;//监控点总数


    public int getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(int deviceNum) {
        this.deviceNum = deviceNum;
    }

    /**
     * 获取 监控点总数
     */
    public int getSignalNum() {
        return this.signalNum;
    }

    /**
     * 设置 监控点总数
     */
    public void setSignalNum(int signalNum) {
        this.signalNum = signalNum;
    }
}
