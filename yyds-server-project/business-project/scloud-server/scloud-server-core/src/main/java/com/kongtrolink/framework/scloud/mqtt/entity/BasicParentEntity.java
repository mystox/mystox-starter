package com.kongtrolink.framework.scloud.mqtt.entity;

/**
 * 获取到的父资产 信息
 *  中台资管->SCloud
 * Created by Eric on 2020/3/4.
 */
public class BasicParentEntity {

    private String name;    //父资产类型
    private String sn;  //父资产SN

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
