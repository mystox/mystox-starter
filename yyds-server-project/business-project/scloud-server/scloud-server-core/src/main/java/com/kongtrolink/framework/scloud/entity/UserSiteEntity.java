package com.kongtrolink.framework.scloud.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 用户管辖的站点 数据实体类
 * Created by Eric on 2020/3/4.
 */
@Document(collection = "user_site")
public class UserSiteEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 8818076279243688716L;
    private String uniqueCode;
    private Integer userId;
    private List<String> siteCodes; //用户所管辖站点的站点编码

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<String> getSiteCodes() {
        return siteCodes;
    }

    public void setSiteCodes(List<String> siteCodes) {
        this.siteCodes = siteCodes;
    }
}
