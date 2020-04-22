package com.kongtrolink.framework.scloud.entity;

/**
 * 关联设备 信息
 *  [使用场景]：站点列表和设备列表 点击显示关联设备
 * Created by Eric on 2020/3/17.
 */
public class RelatedDeviceInfo {

    private String deviceName;  //设备名称
    private String deviceCode;  //设备编码
    private String type;    //设备类型
    private String typeCode;    //设备类型编码
    private String model;   //设备型号
    private String manufacturer;    //生产厂家

    //下面为FSU动环主机设备特有属性
    private String state;	//注册状态（FSU类型、摄像机类型）：在线、离线
    private String operationState;	//运行状态（FSU类型）：工程态、测试态、交维态


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOperationState() {
        return operationState;
    }

    public void setOperationState(String operationState) {
        this.operationState = operationState;
    }
}
