package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;
import java.util.Date;

/**
 * 在线调试修改
 * by Mag on 6/29/2018.
 */
public class RoomSignalThresholdLog implements Serializable{

    private static final long serialVersionUID = 442154807575001948L;
    private String id;
    private String uniqueCode;//企业编码
    private String siteId; //站点ID
    private String fsuId; //FSU ID
    private String deviceId; //设备ID
    private String signalId; //信号点ID
    private double beThreshold; //修改前阀值
    private double afThreshold; //修改后阀值
    private int reMinutes; //恢复分钟
    private Date reTime; //恢复时间
    private String userId;//操作人
    private Date ctime;  //修改时间
    private int result;  //恢复结果 0=未 1=恢复成功 -1 恢复失败

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public double getBeThreshold() {
        return beThreshold;
    }

    public void setBeThreshold(double beThreshold) {
        this.beThreshold = beThreshold;
    }

    public double getAfThreshold() {
        return afThreshold;
    }

    public void setAfThreshold(double afThreshold) {
        this.afThreshold = afThreshold;
    }

    public Date getReTime() {
        return reTime;
    }

    public void setReTime(Date reTime) {
        this.reTime = reTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getReMinutes() {
        return reMinutes;
    }

    public void setReMinutes(int reMinutes) {
        this.reMinutes = reMinutes;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
