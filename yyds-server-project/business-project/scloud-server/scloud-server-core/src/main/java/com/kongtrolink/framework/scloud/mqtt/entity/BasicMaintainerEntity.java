package com.kongtrolink.framework.scloud.mqtt.entity;

/**
 * 向【云管】添加维护人员的基本信息 数据实体类
 * Created by Eric on 2020/4/21.
 */
public class BasicMaintainerEntity {

    private String name;    //姓名
    private String username;    //用户名
    private String phone;   //电话
    private String email;
    private String password;    //密码
    private String currentPostId;   //角色Id
    private String currentPositionName; //角色名称

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrentPostId() {
        return currentPostId;
    }

    public void setCurrentPostId(String currentPostId) {
        this.currentPostId = currentPostId;
    }

    public String getCurrentPositionName() {
        return currentPositionName;
    }

    public void setCurrentPositionName(String currentPositionName) {
        this.currentPositionName = currentPositionName;
    }
}
