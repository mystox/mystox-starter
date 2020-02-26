package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;

/**
 * 设备信号类型映射表导出 数据实体类
 * Created by Eric on 2020/2/20.
 */
public class DeviceTypeExport implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 8478999901444322496L;
    private String deviceTypeName;    //设备类型名称
    private String deviceTypeCode;    //设备类型编号
    private String signalTypeName;  //信号点类型名称
    private String signalTypeCode;  //信号点类型编号
    private String measurement; //单位
    private String cntbId;  //铁塔信号ID
    private String type;    //信号点类型（遥调、遥控、遥测、遥信）
    private Integer communicationError;

    public DeviceTypeExport(){
    }

    public DeviceTypeExport(String deviceTypeName, String deviceTypeCode, SignalType signalType) {
        this.deviceTypeName = deviceTypeName;
        this.deviceTypeCode = deviceTypeCode;
        if (signalType != null) {
            this.signalTypeName = signalType.getTypeName();
            this.signalTypeCode = signalType.getCode();
            this.measurement = signalType.getMeasurement();
            this.cntbId = signalType.getCntbId();
            this.type = signalType.getType();
            if (signalType.isCommunicationError())
                this.communicationError = 1;
        }
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public String getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(String deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public String getSignalTypeName() {
        return signalTypeName;
    }

    public void setSignalTypeName(String signalTypeName) {
        this.signalTypeName = signalTypeName;
    }

    public String getSignalTypeCode() {
        return signalTypeCode;
    }

    public void setSignalTypeCode(String signalTypeCode) {
        this.signalTypeCode = signalTypeCode;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCommunicationError() {
        return communicationError;
    }

    public void setCommunicationError(Integer communicationError) {
        this.communicationError = communicationError;
    }
}
