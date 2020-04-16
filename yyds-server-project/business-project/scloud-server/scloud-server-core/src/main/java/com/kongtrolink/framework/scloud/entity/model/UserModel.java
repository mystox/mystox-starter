package com.kongtrolink.framework.scloud.entity.model;


import java.io.Serializable;
import java.util.Date;


/**
 * 系统用户 前端显示模型
 * Created by Yu Pengtao on 2020/4/13.
 */
public class UserModel implements Serializable {

    private static final long serialVersionUID = 6585555167159051057L;
    private String id;
    private String userId;
    private String name;        //姓名
    private String username;        //账号
    private String phone;   //电话
    private String email;   //邮箱
    private String currentPostId;   //角色id
    private String currentRoleName;     //角色名称
    private Object informRule;

//    private String lockStatus;      //锁定状态
    private String userStatus;      //用户状态
    private Date validTime;       //有效日期
    private String workId;      //员工工号
    private String createTime;      //创建日期
//    private String lastLogin;       //最后登录日期
//    private String changeTime;      //密码更改日期
    private String remark;      //用户备注
    private String password;
    private String gender;     //性别
    private String userTime;

    public String getUserTime() {
        return userTime;
    }

    public void setUserTime(String userTime) {
        this.userTime = userTime;
    }

    public String getCurrentRoleName() {
        return currentRoleName;
    }

    public void setCurrentRoleName(String currentRoleName) {
        this.currentRoleName = currentRoleName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

//    public String getLastLogin() {
//        return lastLogin;
//    }
//
//    public void setLastLogin(String lastLogin) {
//        this.lastLogin = lastLogin;
//    }
//
//    public String getChangeTime() {
//        return changeTime;
//    }
//
//    public void setChangeTime(String changeTime) {
//        this.changeTime = changeTime;
//    }

//    public String getLockStatus() {
//        return lockStatus;
//    }
//
//    public void setLockStatus(String lockStatus) {
//        this.lockStatus = lockStatus;
//    }


    public String getUserId() {
        return userId;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getCurrentPostId() {
        return currentPostId;
    }

    public void setCurrentPostId(String currentPostId) {
        this.currentPostId = currentPostId;
    }

    public Object getInformRule() {
        return informRule;
    }

    public void setInformRule(Object informRule) {
        this.informRule = informRule;
    }
}
