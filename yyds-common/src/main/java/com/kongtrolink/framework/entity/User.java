/**
 * *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************
 */
package com.kongtrolink.framework.entity;

import java.io.Serializable;

/**
 * @auther: liudd
 * @date: 2019/10/11 14:08
 * 功能描述:暂时添加user类型
 */
public class User implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 3528656064038544536L;
	private String _id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String name;
    private String department;
    private String job;         //职位

    private String classes;	// 用户类别：包括企业用户、代理商用户和技术支持用户
    private String type;	// 转换为企业用户的用户类型：暂时包括普通用户和管理员用户
    private String userGroup;   //用户类型为“普通用户”时，有用户组这个字段
    private String companyId;	    // 公司：包括 Enterprise 和 Agency
    private String receiveAlarmMsg = "0";//接收告警短信
    private String receiveAlarmEmail = "0";  //接收告警邮件
    private String receiveAlarmPush = "0";    //是否接收告警推送
    private String receiveWorkPush = "0";     //是否接收工单推送
    private String currentOrgId;                //当前页面所属组织id
    private String currentOrgType;              //当前页面所属组织类型
    private String currentRoleId;               //当前页面所属角色id
    private String currentRouteMark;            //当前页面所在服务路由标记

    public String getCurrentRouteMark()
    {
        return currentRouteMark;
    }

    public void setCurrentRouteMark(String currentRouteMark)
    {
        this.currentRouteMark = currentRouteMark;
    }

    public String getCurrentRoleId()
    {
        return currentRoleId;
    }

    public void setCurrentRoleId(String currentRoleId)
    {
        this.currentRoleId = currentRoleId;
    }

    public String getCurrentOrgType()
    {
        return currentOrgType;
    }

    public void setCurrentOrgType(String currentOrgType)
    {
        this.currentOrgType = currentOrgType;
    }


    public String getCurrentOrgId()
    {
        return currentOrgId;
    }

    public void setCurrentOrgId(String currentOrgId)
    {
        this.currentOrgId = currentOrgId;
    }

    private Boolean adminMark;

    public Boolean getAdminMark()
    {
        return adminMark;
    }

    public void setAdminMark(Boolean adminMark)
    {
        this.adminMark = adminMark;
    }


    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getReceiveAlarmMsg() {
        return receiveAlarmMsg;
    }

    public void setReceiveAlarmMsg(String receiveAlarmMsg) {
        this.receiveAlarmMsg = receiveAlarmMsg;
    }

    public String getReceiveAlarmEmail() {
        return receiveAlarmEmail;
    }

    public void setReceiveAlarmEmail(String receiveAlarmEmail) {
        this.receiveAlarmEmail = receiveAlarmEmail;
    }

    public String getReceiveAlarmPush() {
        return receiveAlarmPush;
    }

    public void setReceiveAlarmPush(String receiveAlarmPush) {
        this.receiveAlarmPush = receiveAlarmPush;
    }

    public String getReceiveWorkPush() {
        return receiveWorkPush;
    }

    public void setReceiveWorkPush(String receiveWorkPush) {
        this.receiveWorkPush = receiveWorkPush;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", classes='" + classes + '\'' +
                ", type='" + type + '\'' +
                ", userGroup='" + userGroup + '\'' +
                ", companyId='" + companyId + '\'' +
                ", receiveAlarmMsg='" + receiveAlarmMsg + '\'' +
                ", receiveAlarmEmail='" + receiveAlarmEmail + '\'' +
                ", receiveAlarmPush='" + receiveAlarmPush + '\'' +
                ", receiveWorkPush='" + receiveWorkPush + '\'' +
                '}';
    }
}
