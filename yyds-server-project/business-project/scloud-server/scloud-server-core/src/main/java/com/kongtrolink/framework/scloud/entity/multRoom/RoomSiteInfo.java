package com.kongtrolink.framework.scloud.entity.multRoom;


import com.kongtrolink.framework.scloud.entity.SiteEntity;

import java.io.Serializable;

/**
 * 基本信息
 * Created by Mg on 6/20/2018.
 */
public class RoomSiteInfo implements Serializable {

    private static final long serialVersionUID = 5210704695095804208L;

    private String stationType;//基站类型

    private String coordinate;//经纬度

    private String address;//地址

    private String respName;//维护人员

    private String respPhone;//电话号码

    private int deviceNum;//监控设备总数

    private int signalNum;//监控点总数


    public void inputSite(SiteEntity site){
        if(site ==null){
            return;
        }
        this.coordinate = site.getCoordinate();
        this.address = site.getAddress();
        this.respName  = site.getRespName();
        this.respPhone = site.getRespPhone();
        this.stationType = site.getSiteType();
    }
    /**
     * 获取 基站类型
     */
    public String getStationType() {
        return this.stationType;
    }

    /**
     * 设置 基站类型
     */
    public void setStationType(String stationType) {
        this.stationType = stationType;
    }

    /**
     * 获取 经纬度
     */
    public String getCoordinate() {
        return this.coordinate;
    }

    /**
     * 设置 经纬度
     */
    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * 获取 地址
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * 设置 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取 维护人员
     */
    public String getRespName() {
        return this.respName;
    }

    /**
     * 设置 维护人员
     */
    public void setRespName(String respName) {
        this.respName = respName;
    }

    /**
     * 获取 电话号码
     */
    public String getRespPhone() {
        return this.respPhone;
    }

    /**
     * 设置 电话号码
     */
    public void setRespPhone(String respPhone) {
        this.respPhone = respPhone;
    }

    /**
     * 获取 监控设备总数
     */
    public int getDeviceNum() {
        return this.deviceNum;
    }

    /**
     * 设置 监控设备总数
     */
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
