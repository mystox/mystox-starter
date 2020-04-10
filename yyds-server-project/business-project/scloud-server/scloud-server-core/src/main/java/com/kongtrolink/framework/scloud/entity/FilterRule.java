package com.kongtrolink.framework.scloud.entity;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 14:48
 * @Description:
 */
public class FilterRule {

    private String id;
    private String name;
    private FacadeView creator;
    private String remarks;
    private Date updateTime;

    private String alarmName;
    private List<Integer> alarmLevelList;
    private List<String> deviceTypeList;
    private Date startBeginTime;                //发生开始时间
    private Date startEndTime;                  //发生结束时间
    private Date clearBeginTime;                //清除开始时间
    private Date clearEndTime;                  //清除结束时间
    private List<String> siteCodeList;          //站点编码列表，使用站点编码列表控制区域权限
    private Boolean state = false;

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
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

    public FacadeView getCreator() {
        return creator;
    }

    public void setCreator(FacadeView creator) {
        this.creator = creator;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<String> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<String> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public List<Integer> getAlarmLevelList() {
        return alarmLevelList;
    }

    public void setAlarmLevelList(List<Integer> alarmLevelList) {
        this.alarmLevelList = alarmLevelList;
    }

    public List<String> getDeviceTypeList() {
        return deviceTypeList;
    }

    public void setDeviceTypeList(List<String> deviceTypeList) {
        this.deviceTypeList = deviceTypeList;
    }

    public Date getStartBeginTime() {
        return startBeginTime;
    }

    public void setStartBeginTime(Date startBeginTime) {
        this.startBeginTime = startBeginTime;
    }

    public Date getStartEndTime() {
        return startEndTime;
    }

    public void setStartEndTime(Date startEndTime) {
        this.startEndTime = startEndTime;
    }

    public Date getClearBeginTime() {
        return clearBeginTime;
    }

    public void setClearBeginTime(Date clearBeginTime) {
        this.clearBeginTime = clearBeginTime;
    }

    public Date getClearEndTime() {
        return clearEndTime;
    }

    public void setClearEndTime(Date clearEndTime) {
        this.clearEndTime = clearEndTime;
    }
}
