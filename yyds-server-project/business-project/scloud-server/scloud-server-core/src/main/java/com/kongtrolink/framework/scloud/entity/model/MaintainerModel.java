package com.kongtrolink.framework.scloud.entity.model;

import com.kongtrolink.framework.scloud.entity.MaintainerEntity;

/**
 * 维护用户前端显示模型
 * Created by Eric on 2020/2/28.
 */
public class MaintainerModel extends MaintainerEntity {

    /**
     *
     */
    private static final long serialVersionUID = 5338061324345787329L;
    private String password;    //密码
    private String name;    //名称
    private String phone;   //联系电话
    private String email;   //邮箱
    private String currentPostId;   //角色Id
    private String currentPositionName; //角色名称

    private String isModified;  //修改维护用户时，是否修改了姓名或手机号或邮箱,"0"-未修改，"1"-修改

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsModified() {
        return isModified;
    }

    public void setIsModified(String isModified) {
        this.isModified = isModified;
    }
}
