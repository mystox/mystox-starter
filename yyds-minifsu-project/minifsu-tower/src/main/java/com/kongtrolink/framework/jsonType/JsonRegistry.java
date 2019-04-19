package com.kongtrolink.framework.jsonType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengw
 * 内部平台注册SN
 * 新建文件 2019-4-17 15:57:52
 */
public class JsonRegistry {
    //sn
    private String sn;
    //上报设备列表
    private List<JsonDevice> deviceList;
    //内部平台Ip
    private String innerIp;
    //内部平台端口
    private int innerPort;

    public JsonRegistry() {
        this.deviceList = new ArrayList<>();
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public List<JsonDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<JsonDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public String getInnerIp() {
        return innerIp;
    }

    public void setInnerIp(String innerIp) {
        this.innerIp = innerIp;
    }

    public int getInnerPort() {
        return innerPort;
    }

    public void setInnerPort(int innerPort) {
        this.innerPort = innerPort;
    }
}
