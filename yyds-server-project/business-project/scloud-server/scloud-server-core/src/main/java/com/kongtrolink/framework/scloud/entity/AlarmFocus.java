package com.kongtrolink.framework.scloud.entity;

import com.sun.istack.internal.NotNull;
import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2020/2/28 13:10
 * @Description:告警关注实体类
 */
public class AlarmFocus {

    private String id;
    @NotNull
    private String enterpriseCode;
    @NotNull
    private String serverCode;
    @NotNull
    private String siteCode;
    @NotNull
    private String siteName;
    @NotNull
    private String siteAddress;
    @NotNull
    private String deviceCode;  //设备编码
    @NotNull
    private String deviceName;
    @NotNull
    private String cntbId;      //signalType中cntbId
    @NotNull
    private String signalName;
    private String userId;
    private String username;
    private Date focusTime;
    private String entDevSig;           //enterprise_CodedeviceId_signalId， 用于告警关注，屏蔽等功能

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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }

    public String initEntDevSig(){
        this.entDevSig = enterpriseCode + "_" + deviceCode+ "_" + cntbId;
        return this.entDevSig;
    }

//    public void initSiteInfo(SiteModel siteModel){
//        if(null == siteModel){
//            return;
//        }
//        this.siteName = siteModel.getName();
//        this.siteCode = siteModel.getCode();
//        this.siteAddress = siteModel.getAddress();
//    }
//
//    public void initDeviceInfo(DeviceModel deviceModel){
//        if(null == deviceModel){
//            return;
//        }
//        this.deviceName = deviceModel.getName();
//    }
//
//    public void initSignalInfo(SignalType signalType){
//        if(null == signalType){
//            return;
//        }
//        this.signalName = signalType.getTypeName();
//    }
}
