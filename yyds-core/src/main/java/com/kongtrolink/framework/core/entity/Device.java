package com.kongtrolink.framework.core.entity;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/3/26 16:08
 * @Description:
 */
public class Device {

    private String id;
    //JSON转换使用字段
    private String dev;     //变化上报时dev字段
    private String type;    //设备类型
    private String resNo;   //资源编号
    private List<Signal> signalList;

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

    public List<Signal> getSignalList() {
        return signalList;
    }

    public void setSignalList(List<Signal> signalList) {
        this.signalList = signalList;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
