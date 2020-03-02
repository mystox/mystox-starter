package com.kongtrolink.framework.scloud.mqtt.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 从【资管】获取的站点基本信息 数据实体类
 * Created by Eric on 2020/2/12.
 */
public class BasicSiteEntity {

    @JSONField(name = "enterpriseCode")
    private String uniqueCode;  //企业识别码
    @JSONField(name = "address")
    private String tierCode;    //区域层级编码
    @JSONField(name = "sn")
    private String code;    //站点编码
    @JSONField(name = "siteName")
    private String name;    //站点名称
    @JSONField(name = "siteType")
    private String siteType;    //站点类型
    @JSONField(name = "name")
    private String assetType;    //资产类型

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getTierCode() {
        return tierCode;
    }

    public void setTierCode(String tierCode) {
        this.tierCode = tierCode;
    }

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

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }
}
