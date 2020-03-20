package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.base.GeneratedValue;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

/**
 * 系统用户 数据实体类
 * Created by Eric on 2020/2/28.
 */
public class UserEntity implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 4921460506552100743L;

    private String userId;  //用户Id
    private List<String> siteCodes; //用户管辖站点
    private List<String> tierCodes; //用户管辖区域

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getSiteCodes() {
        return siteCodes;
    }

    public void setSiteCodes(List<String> siteCodes) {
        this.siteCodes = siteCodes;
    }
}
