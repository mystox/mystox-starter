package com.kongtrolink.framework.scloud.mqtt.query;

/**
 * 设备基本信息 查询类
 *  SCloud->中台资管
 * Created by Eric on 2020/2/28.
 */
public class BasicDeviceQuery {
    private BasicCommonQuery serverCode;
    private BasicCommonQuery enterpriseCode;  //企业识别码
    private BasicCommonQuery type;    //资产类型
    private BasicParentQuery _parent;   //父资产信息
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
