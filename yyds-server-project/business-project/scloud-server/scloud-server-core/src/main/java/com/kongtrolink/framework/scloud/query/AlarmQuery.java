package com.kongtrolink.framework.scloud.query;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 15:19
 * @Description:
 */
public class AlarmQuery {

    private String enterpriseCode;
    private String serverCode;
    private String name;                //告警名称
    private Integer targetLevel;         //目标等级
    private String state;               //告警状态(待处理，已消除)
    private Boolean check;              //是否确认
    private String deviceType;          //设备型号，设备类型，与资管一致
    private String deviceModel;         //设备型号，设备型号，如果没有与deviceType一致
    private Date beginTime;             //开始时间
    private Date endTime;               //结束时间
    private List<String> deviceCodeList;  //设备编码列表，用户数据权限最终转换成设备id列表，传递到中台告警模块查询
    private List<String> tierCodeList;  //区域层级编码列表
    private List<String> siteIdList;    //站点编码列表

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<String> getDeviceCodeList() {
        return deviceCodeList;
    }

    public void setDeviceCodeList(List<String> deviceCodeList) {
        this.deviceCodeList = deviceCodeList;
    }

    public List<String> getTierCodeList() {
        return tierCodeList;
    }

    public void setTierCodeList(List<String> tierCodeList) {
        this.tierCodeList = tierCodeList;
    }

    public List<String> getSiteIdList() {
        return siteIdList;
    }

    public void setSiteIdList(List<String> siteIdList) {
        this.siteIdList = siteIdList;
    }
}
