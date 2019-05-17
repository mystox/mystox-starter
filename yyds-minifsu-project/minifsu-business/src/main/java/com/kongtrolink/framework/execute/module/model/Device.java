package com.kongtrolink.framework.execute.module.model;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/3/26 16:08
 * @Description:
 */
public class Device {

    private String id;
    private Integer type;    //设备类型
    private Integer resNo;   //资源编号
    private String port; //设备端口
    private String name; //设备名称
    private String version; //设备版本
    private Integer serialNumber; //设备序列号 | 地址
    @Field("SN")
    private String SN;

    private Date invalidTime; //0为有效


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public Date getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getResNo() {
        return resNo;
    }

    public void setResNo(Integer resNo) {
        this.resNo = resNo;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
