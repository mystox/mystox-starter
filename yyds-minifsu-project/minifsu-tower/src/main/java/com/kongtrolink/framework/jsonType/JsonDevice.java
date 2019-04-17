package com.kongtrolink.framework.jsonType;

import com.kongtrolink.framework.core.entity.Device;

/**
 * @author fengw
 * 铁塔设备ID列表信息
 * 新建文件 2019-4-16 18:15:34
 */
public class JsonDevice {
    //fsuId
    private String fsuId;
    //铁塔设备ID
    private String deviceId;
    //端口
    private String port;
    //设备版本号
    private String type;
    //资源编号
    private String resNo;

    public JsonDevice() {
    }

    public JsonDevice(String fsuId, String deviceId) {
        this.fsuId = fsuId;
        this.deviceId = deviceId;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResNo() {
        return resNo;
    }

    public void setResNo(String resNo) {
        this.resNo = resNo;
    }
}
