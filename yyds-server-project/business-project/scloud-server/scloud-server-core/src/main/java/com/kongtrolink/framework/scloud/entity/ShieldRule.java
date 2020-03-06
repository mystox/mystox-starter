package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/4 14:42
 * @Description:
 */
public class ShieldRule {

    private String id;
    private String name;
    private List<String> deviceIdList;
    private List<String> alarmlevel;
    private String reason;
    private FacadeView creator;
    private Date updateTime;
    private Boolean enabled = false;
    //前端详情展示用
    private List<SiteModel> siteModelList = new ArrayList<>();
    private List<DeviceModel> deviceModelList = new ArrayList<>();

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<SiteModel> getSiteModelList() {
        return siteModelList;
    }

    public void setSiteModelList(List<SiteModel> siteModelList) {
        this.siteModelList = siteModelList;
    }

    public List<DeviceModel> getDeviceModelList() {
        return deviceModelList;
    }

    public void setDeviceModelList(List<DeviceModel> deviceModelList) {
        this.deviceModelList = deviceModelList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDeviceIdList() {
        return deviceIdList;
    }

    public void setDeviceIdList(List<String> deviceIdList) {
        this.deviceIdList = deviceIdList;
    }

    public List<String> getAlarmlevel() {
        return alarmlevel;
    }

    public void setAlarmlevel(List<String> alarmlevel) {
        this.alarmlevel = alarmlevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public FacadeView getCreator() {
        return creator;
    }

    public void setCreator(FacadeView creator) {
        this.creator = creator;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
