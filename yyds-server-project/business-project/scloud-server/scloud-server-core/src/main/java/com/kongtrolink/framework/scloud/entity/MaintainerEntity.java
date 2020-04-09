package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;

/**
 * 维护用户(扩展信息) 数据实体类
 * Created by Eric on 2020/2/28.
 */
public class MaintainerEntity implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = -8412200403230695176L;

    private String userId;  //用户Id
    private String username;    //账号

    private String companyName; //代维公司
    private String organizationId;  //组织机构Id
    private String status;  //在职状态(在职 or 离职)
    private Long hireDate;  //入职时间
    private Long expireDate;    //合同到期时间
    private String major;   //代维专业
    private String skill;   //具备技能
    private String address; //家庭地址
    private String idCard;  //身份证号码
    private String sex; //性别
    private String duty;    //岗位职责
    private String education;   //文化程度(小学、初中、高中、初中中专、高中中专、大专、本科、研究生)
    private String authentication;    //认证名称
    private String authLevel;    //认证级别
    private Long authDate;  //认证获取时间
    private Long authExpireDate;  //认证有效期

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getHireDate() {
        return hireDate;
    }

    public void setHireDate(Long hireDate) {
        this.hireDate = hireDate;
    }

    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Long expireDate) {
        this.expireDate = expireDate;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getAuthLevel() {
        return authLevel;
    }

    public void setAuthLevel(String authLevel) {
        this.authLevel = authLevel;
    }

    public Long getAuthDate() {
        return authDate;
    }

    public void setAuthDate(Long authDate) {
        this.authDate = authDate;
    }

    public Long getAuthExpireDate() {
        return authExpireDate;
    }

    public void setAuthExpireDate(Long authExpireDate) {
        this.authExpireDate = authExpireDate;
    }
}
