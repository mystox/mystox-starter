package com.kongtrolink.framework.core.entity;

/**
 * @Auther: liudd
 * @Date: 2019/3/26 16:08
 * @Description:
 */
public class Device {

    private String id;
    private String type;    //设备类型
    private String resNo;   //资源编号

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
