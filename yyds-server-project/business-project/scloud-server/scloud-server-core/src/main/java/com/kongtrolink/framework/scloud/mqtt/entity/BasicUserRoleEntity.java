package com.kongtrolink.framework.scloud.mqtt.entity;

/**
 * 从【云管】获取的角色信息
 * Created by Eric on 2020/4/21.
 */
public class BasicUserRoleEntity {
    private String id;  //角色Id
    private String name;    //角色名称

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
}
