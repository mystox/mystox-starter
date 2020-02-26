package com.kongtrolink.framework.scloud.mqtt.query;

import java.util.List;

/**
 * 站点基本信息 查询类
 * Created by Eric on 2020/2/24.
 */
public class BasicSiteQuery {

    private String enterpriseCode;  //企业识别码
    private String type;    //资产类型
    private List<String> addressCodes;  //区域编码
    private String name;    //站点名称
    private String siteType;    //站点类型

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getAddressCodes() {
        return addressCodes;
    }

    public void setAddressCodes(List<String> addressCodes) {
        this.addressCodes = addressCodes;
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
}
