package com.kongtrolink.framework.mqtt;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/31 16:38
 * @Description:
 */
public class UserInfo {

    private String id;
    private String username;
    private String phone;
    private String email;
    List<String> region;

    public List<String> getRegion() {
        return region;
    }

    public void setRegion(List<String> region) {
        this.region = region;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
