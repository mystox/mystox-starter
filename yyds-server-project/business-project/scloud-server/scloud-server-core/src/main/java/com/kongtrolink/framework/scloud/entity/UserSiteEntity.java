package com.kongtrolink.framework.scloud.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 用户管辖的站点 数据实体类
 * Created by Eric on 2020/3/4.
 */
public class UserSiteEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8818076279243688716L;
    private String userId;  //用户ID
    private String siteCode; //管辖站点的站点编码
    private Integer siteId; //管辖站点的站点ID

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }
}
