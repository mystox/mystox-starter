package com.kongtrolink.framework.scloud.entity.model;

/**
 * 设备类型 前端显示
 * Created by Eric on 2020/3/17.
 */
public class DeviceTypeModel {

    private String type;    //设备类型
    private String typeCode;    //类型编码

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
}
