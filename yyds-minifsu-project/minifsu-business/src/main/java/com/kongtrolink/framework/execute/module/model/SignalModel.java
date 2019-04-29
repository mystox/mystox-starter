package com.kongtrolink.framework.execute.module.model;

/**
 * Created by mystoxlol on 2019/4/15, 19:05.
 * company: kongtrolink
 * description:
 * update record:
 */
public class SignalModel {
    private String id;
    private String dataId;
    private String name;
    private Integer type;
    private Integer deviceType;
    private String unit;
    private String snModel;
    private Integer valueBase = 1; //信号点基数

    public String getSnModel() {
        return snModel;
    }

    public void setSnModel(String snModel) {
        this.snModel = snModel;
    }

    public Integer getValueBase() {
        return valueBase;
    }

    public void setValueBase(Integer valueBase) {
        this.valueBase = valueBase;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "SignalModel{" +
                "id='" + id + '\'' +
                ", dataId='" + dataId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", deviceType=" + deviceType +
                ", unit='" + unit + '\'' +
                ", snModel='" + snModel + '\'' +
                ", valueBase=" + valueBase +
                '}';
    }
}
