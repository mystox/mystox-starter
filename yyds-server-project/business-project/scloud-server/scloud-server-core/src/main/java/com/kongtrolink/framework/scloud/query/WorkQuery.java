package com.kongtrolink.framework.scloud.query;

import com.kongtrolink.framework.scloud.entity.WorkAlarm;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 11:26
 * @Description:
 */
public class WorkQuery extends Paging{

    private String id;
    private String sendType;            //派单类型
    private String status;              //工单状态
    private String code;                //流水号
    private String siteCode;            //站点编码
    private String siteName;            //站点名称
    private String siteType;	        //站点类型
    private List<String> siteCodeList;  //站点编码列表
    private String deviceCode;          //设备编码
    private String deviceName;          //设备名称
    private String deviceType;          //设备类型
    private String alarmLevel;          //告警等级
    private String companyName;         //代维公司
    private Date sentTimeBegin;         //派单开始时间
    private Date sentTimeEnd;           //派单开始时间
    private WorkAlarm workAlarm;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public WorkAlarm getWorkAlarm() {
        return workAlarm;
    }

    public void setWorkAlarm(WorkAlarm workAlarm) {
        this.workAlarm = workAlarm;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public List<String> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<String> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getSentTimeBegin() {
        return sentTimeBegin;
    }

    public void setSentTimeBegin(Date sentTimeBegin) {
        this.sentTimeBegin = sentTimeBegin;
    }

    public Date getSentTimeEnd() {
        return sentTimeEnd;
    }

    public void setSentTimeEnd(Date sentTimeEnd) {
        this.sentTimeEnd = sentTimeEnd;
    }
}
