package com.kongtrolink.framework.scloud.mqtt.entity;

/**
 * 从【云管】获取的企业基本信息 数据实体类
 * Created by Eric on 2020/2/19.
 */
public class BasicCompanyEntity {

    private String code;    //企业识别码
    private String name;    //企业名称

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
