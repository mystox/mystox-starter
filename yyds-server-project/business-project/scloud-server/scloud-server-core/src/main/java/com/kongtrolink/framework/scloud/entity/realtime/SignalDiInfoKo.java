package com.kongtrolink.framework.scloud.entity.realtime;


import com.kongtrolink.framework.scloud.entity.Device;
import com.kongtrolink.framework.scloud.entity.Site;

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

    private Map<String, Site> siteMap = new HashMap<>();
    private Map<String, Device> deviceMap = new HashMap<>();
    private List<Integer> siteIds = new ArrayList<>();
    private List<Integer> deviceIds = new ArrayList<>();

    public Map<String, Site> getSiteMap() {
        return siteMap;
    }

    public void setSiteMap(Map<String, Site> siteMap) {
        this.siteMap = siteMap;
    }

    public Map<String, Device> getDeviceMap() {
        return deviceMap;
    }

    public void setDeviceMap(Map<String, Device> deviceMap) {
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
