package com.kongtrolink.framework.scloud.mqtt.query;

import java.util.List;

/**
 * 站点基本信息 查询类
 *  SCloud->中台资管
 * Created by Eric on 2020/2/24.
 */
public class BasicSiteQuery {
    private BasicCommonQuery serverCode;
    private BasicCommonQuery enterpriseCode;  //企业识别码
    private BasicCommonQuery name;    //资产类型
    private BasicCommonQuery address;   //区域编码
    private BasicCommonQuery sn;  //父资产SN：此时为 站点编码（模糊搜索）
    private BasicCommonQuery siteName;    //站点名称（模糊搜索）
    private BasicCommonQuery siteType;    //站点类型

    public BasicCommonQuery getServerCode() {
        return serverCode;
    }

    public void setServerCode(BasicCommonQuery serverCode) {
        this.serverCode = serverCode;
    }

    public BasicCommonQuery getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(BasicCommonQuery enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public BasicCommonQuery getName() {
        return name;
    }

    public void setName(BasicCommonQuery name) {
        this.name = name;
    }

    public BasicCommonQuery getAddress() {
        return address;
    }

    public void setAddress(BasicCommonQuery address) {
        this.address = address;
    }

    public BasicCommonQuery getSn() {
        return sn;
    }

    public void setSn(BasicCommonQuery sn) {
        this.sn = sn;
    }

    public BasicCommonQuery getSiteName() {
        return siteName;
    }

    public void setSiteName(BasicCommonQuery siteName) {
        this.siteName = siteName;
    }

    public BasicCommonQuery getSiteType() {
        return siteType;
    }

    public void setSiteType(BasicCommonQuery siteType) {
        this.siteType = siteType;
    }
}
