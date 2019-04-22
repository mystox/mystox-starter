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
    private String type;
    private String deviceType;
    private String unit;

    private Integer valueBase = 1; //信号点基数

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
