package com.kongtrolink.framework.scloud.mqtt.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 从【云管】获取的设备基本信息 数据实体类
 * Created by Eric on 2020/2/12.
 */
public class BasicDeviceEntity {

    @JSONField(name = "serverCode")
    private String serverCode;
    @JSONField(name = "enterpriseCode")
    private String uniqueCode;  //企业识别码
    @JSONField(name = "type")
    private String assetType;   //资产类型
    @JSONField(name = "sn")
    private String code;    //设备编码
    @JSONField(name = "deviceName")
    private String deviceName;  //设备名称
    @JSONField(name = "model")
    private String model;   //设备型号
    @JSONField(name = "_parent")
    private BasicParentEntity _parent;  //父资产

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BasicParentEntity get_parent() {
        return _parent;
    }

    public void set_parent(BasicParentEntity _parent) {
        this._parent = _parent;
    }
}
