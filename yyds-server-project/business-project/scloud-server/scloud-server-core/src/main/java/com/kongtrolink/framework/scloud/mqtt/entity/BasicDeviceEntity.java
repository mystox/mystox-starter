package com.kongtrolink.framework.scloud.mqtt.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 从【云管】获取的设备基本信息 数据实体类
 * Created by Eric on 2020/2/12.
 */
public class BasicDeviceEntity {

    @JSONField(name = "enterpriseCode")
    private String uniqueCode;  //企业识别码
    @JSONField(name = "address")
    private String tierCode;    //区域编码
    @JSONField(name = "siteCode")
    private String siteCode;    //站点编码
    @JSONField(name = "sn")
    private String deviceCode;  //设备编码
    @JSONField(name = "name")
    private String name;    //设备名称
    @JSONField(name = "model")
    private String model;   //设备型号
    @JSONField(name = "type")
    private String type;    //资产类型

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getTierCode() {
        return tierCode;
    }

    public void setTierCode(String tierCode) {
        this.tierCode = tierCode;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
