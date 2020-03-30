/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.framework.scloud.entity.config3d;

import java.util.List;
/**
 * copy form scloud
 * @author Mag
 */
public class ConfigAppLocateMap {
    
    private String siteId;
    private List<ConfigAppDeviceLocate> devices;    // 站内设备及对应坐标
    private List<ConfigAppRoom> rooms;  // 背景房间（区域）划分

    public ConfigAppLocateMap() {
    }

    public ConfigAppLocateMap(String siteId, List<ConfigAppDeviceLocate> devices, List<ConfigAppRoom> rooms) {
        this.siteId = siteId;
        this.devices = devices;
        this.rooms = rooms;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public List<ConfigAppDeviceLocate> getDevices() {
        return devices;
    }

    public void setDevices(List<ConfigAppDeviceLocate> devices) {
        this.devices = devices;
    }

    public List<ConfigAppRoom> getRooms() {
        return rooms;
    }

    public void setRooms(List<ConfigAppRoom> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "ConfigAppLocateMap{" + "siteId=" + siteId + ", devices=" + devices + ", rooms=" + rooms + '}';
    }
    
}
