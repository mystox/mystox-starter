package com.kongtrolink.framework.scloud.mqtt.query;

import java.util.List;

/**
 * 站点基本信息 查询类
 * Created by Eric on 2020/2/24.
 */
public class BasicSiteQuery {
    private String serverCode;
    private String enterpriseCode;  //企业识别码
    private String name;    //资产类型
    private List<String> address;   //区域编码
    private String sn;  //站点编码（模糊搜索）
    private String siteName;    //站点名称（模糊搜索）
    private String siteType;    //站点类型

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }
}
