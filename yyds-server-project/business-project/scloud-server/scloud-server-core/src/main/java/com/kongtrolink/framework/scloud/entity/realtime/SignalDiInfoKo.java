package com.kongtrolink.framework.scloud.entity.realtime;


import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.SiteEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mg on 2018/5/25.
 */
public class SignalDiInfoKo implements Serializable{
    private static final long serialVersionUID = 4292212058977817974L;

    private Map<String, SiteEntity> siteMap = new HashMap<>();
    private Map<String, DeviceEntity> deviceMap = new HashMap<>();
    private List<Integer> siteIds = new ArrayList<>();
    private List<Integer> deviceIds = new ArrayList<>();

    public Map<String, SiteEntity> getSiteMap() {
        return siteMap;
    }

    public void setSiteMap(Map<String, SiteEntity> siteMap) {
        this.siteMap = siteMap;
    }

    public Map<String, DeviceEntity> getDeviceMap() {
        return deviceMap;
    }

    public void setDeviceMap(Map<String, DeviceEntity> deviceMap) {
        this.deviceMap = deviceMap;
    }

    public List<Integer> getSiteIds() {
        return siteIds;
    }

    public void setSiteIds(List<Integer> siteIds) {
        this.siteIds = siteIds;
    }

    public List<Integer> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<Integer> deviceIds) {
        this.deviceIds = deviceIds;
    }
}
