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
}
