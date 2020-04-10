package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;
import java.util.Date;

/**
 *  综合机房 操作日志
 * by Mag on 6/20/2018.
 */
public class RoomOperateLog implements Serializable {

    private static final long serialVersionUID = 1807534699665343710L;

    private String logType; //日志类型
    private String siteId;//站点ID
    private String siteName;    //站点名称
    private String tierName;//区域
    private String tierCode;//区域code
    private String userName;//操作用户
    private String userId;//操作用户ID
    private String deviceId;// 操作设备ID
    private String deviceName;// 操作设备名称
    private String signalName;//信号点名称
    private String operate;//操作内容
    private Date ctime;// 操作时间
    private String model;//操作模块：综合机房、数据监控

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public String getTierCode() {
        return tierCode;
    }

    public void setTierCode(String tierCode) {
        this.tierCode = tierCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
