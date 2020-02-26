package com.kongtrolink.framework.scloud.mqtt.entity;

/**
 * 从【云管】获取的用户基本信息 数据实体类
 * Created by Eric on 2020/2/19.
 */
public class BasicUserEntity {

    private String id;  //用户Id
    private String name;    //用户名称
    private String phone;
    private String currentRoleId;  //角色Id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrentRoleId() {
        return currentRoleId;
    }

    public void setCurrentRoleId(String currentRoleId) {
        this.currentRoleId = currentRoleId;
    }
}
