package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2020/2/28 13:10
 * @Description:告警关注实体类
 */
public class AlarmFocus {

    private String id;
    private String enterpriseCode;
    private String serverCode;
    private int siteId;
    private String deviceId;  //设备编码
    private String signalId;      //signalType中cntbId
    private String signalName;
    private String userId;
    private String username;
    private Date focusTime;
    private String entDevSig;           //enterprise_CodedeviceId_signalId， 用于告警关注，屏蔽等功能

    private String siteCode;
    private String siteName;
    private String siteAddress;
    private String deviceName;

    public String getEntDevSig() {
        return entDevSig;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public void setEntDevSig(String entDevSig) {
        this.entDevSig = entDevSig;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public Date getFocusTime() {
        return focusTime;
    }

    public void setFocusTime(Date focusTime) {
        this.focusTime = focusTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String initEntDevSig(){
        this.entDevSig = enterpriseCode + "_" + deviceId+ "_" +signalId;
        return this.entDevSig;
    }

    public void initSiteInfo(SiteModel siteModel){
        if(null == siteModel){
            return;
        }
        this.siteName = siteModel.getName();
        this.siteCode = siteModel.getCode();
        this.siteAddress = siteModel.getAddress();
    }

    public void initDeviceInfo(DeviceModel deviceModel){
        if(null == deviceModel){
            return;
        }
        this.siteId = deviceModel.getSiteId();
        this.deviceName = deviceModel.getName();
    }

    public void initSignalInfo(SignalType signalType){
        if(null == signalType){
            return;
        }
        this.signalName = signalType.getTypeName();
    }
}
