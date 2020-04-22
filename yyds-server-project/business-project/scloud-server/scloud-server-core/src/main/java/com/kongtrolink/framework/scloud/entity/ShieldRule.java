package com.kongtrolink.framework.scloud.entity;

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
    private String enterpriseCode;
    private String serverCode;
    private String name;
    private List<String> deviceCodeList;
    private List<Integer> alarmlevelList;       //原始告警等级
    private String reason;
    private FacadeView creator;
    private Date updateTime;
    private Boolean enabled = false;
    private List<String> deviceInfoList = new ArrayList<>();        //前端展示专用

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public List<String> getDeviceInfoList() {
        return deviceInfoList;
    }

    public void setDeviceInfoList(List<String> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public List<String> getDeviceCodeList() {
        return deviceCodeList;
    }

    public void setDeviceCodeList(List<String> deviceCodeList) {
        this.deviceCodeList = deviceCodeList;
    }

    public List<Integer> getAlarmlevelList() {
        return alarmlevelList;
    }

    public void setAlarmlevelList(List<Integer> alarmlevelList) {
        this.alarmlevelList = alarmlevelList;
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
