package com.kongtrolink.framework.scloud.mqtt.query;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 设备基本信息 查询类
 *  SCloud->中台资管
 * Created by Eric on 2020/2/28.
 */
public class BasicDeviceQuery {
    private BasicCommonQuery serverCode;
    private BasicCommonQuery enterpriseCode;  //企业识别码
    private BasicCommonQuery type;    //资产类型
    @JSONField(name = "_parent")    //(勿删。加该注解是为了防止fastJson解析时下划线消失的问题)
    private BasicParentQuery _parent;   //父资该注解是为了防止fastJson解析时下划线消失的问题)产信息
    @JSONField(name = "_notParent") //(勿删。加
    private BasicParentQuery _notParent;    //未关联的资产信息
    private BasicCommonQuery sn;    //资产SN:此时为 设备编码
    private BasicCommonQuery deviceName;    //设备名称
    private BasicCommonQuery model; //设备型号

    public BasicCommonQuery getServerCode() {
        return serverCode;
    }

    public void setServerCode(BasicCommonQuery serverCode) {
        this.serverCode = serverCode;
    }

    public BasicCommonQuery getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(BasicCommonQuery enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public BasicCommonQuery getType() {
        return type;
    }

    public void setType(BasicCommonQuery type) {
        this.type = type;
    }

    public BasicParentQuery get_parent() {
        return _parent;
    }

    public void set_parent(BasicParentQuery _parent) {
        this._parent = _parent;
    }

    public BasicParentQuery get_notParent() {
        return _notParent;
    }

    public void set_notParent(BasicParentQuery _notParent) {
        this._notParent = _notParent;
    }

    public BasicCommonQuery getSn() {
        return sn;
    }

    public void setSn(BasicCommonQuery sn) {
        this.sn = sn;
    }

    public BasicCommonQuery getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(BasicCommonQuery deviceName) {
        this.deviceName = deviceName;
    }

    public BasicCommonQuery getModel() {
        return model;
    }

    public void setModel(BasicCommonQuery model) {
        this.model = model;
    }
}
