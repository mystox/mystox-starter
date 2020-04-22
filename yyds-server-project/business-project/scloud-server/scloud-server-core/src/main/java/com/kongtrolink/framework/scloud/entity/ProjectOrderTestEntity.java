package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;

/**
 * 工程管理-测试单-测试项 数据实体类
 * Created by Eric on 2020/4/17.
 */
public class ProjectOrderTestEntity implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -3493159320939065735L;
    private String _id; //测试项Id
    private String orderId; //订单Id
    private String deviceName;  //设备名称
    private String deviceCode;  //设备Code
    private String cntbId;  //信号点cntbId
    private String signalType;  //信号类型
    private String signalName;  //信号点名称
    private String history; //历史状态（针对遥信点历史告警）
    private Double current; //当前状态值
    private String measurement; //单位
    private Boolean result; //测试结果
    private String remark;  //备注

    public ProjectOrderTestEntity(){
    }

    public ProjectOrderTestEntity(String orderId, String deviceName, String deviceCode, String cntbId, String signalType, String signalName, String measurement) {
        this.orderId = orderId;
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
        this.cntbId = cntbId;
        this.signalType = signalType;
        this.signalName = signalName;
        this.measurement = measurement;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public String getSignalType() {
        return signalType;
    }

    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
