package com.kongtrolink.framework.core.entity;

/**
 * @Auther: liudd
 * @Date: 2019/3/26 16:08
 * @Description:
 */
public class Device {

    private String id;
    private Integer type;    //设备类型
    private Integer resNo;   //资源编号
    private Integer port; //设备端口
    private String name; //设备名称
    private String version; //设备版本
    private String serialNumber; //设备序列号 | 地址
    private String SN;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    private Integer delFlag; //无效标记 1 有效 0 无效

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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}
