package com.kongtrolink.framework.reports.entity.query;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/9 16:35
 * \* Description:
 * \
 */
public class SiteEntity {
    @JSONField(name = "sn")
    private String siteId; //站点id 即sn
    private String siteName;
    private String address;
    private String type;
    private String siteType;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }
}