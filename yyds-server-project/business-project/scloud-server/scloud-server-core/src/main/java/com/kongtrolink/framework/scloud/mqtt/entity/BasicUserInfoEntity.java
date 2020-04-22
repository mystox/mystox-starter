package com.kongtrolink.framework.scloud.mqtt.entity;

/**
 *
 * Created by Eric on 2020/4/21.
 */
public class BasicUserInfoEntity {

    private String username;    //账号
    private String userId;  //用户Id

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
