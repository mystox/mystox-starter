package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统用户(扩展信息) 数据实体类
 * Created by Yu Pengtao on 2020/4/13.
 */
public class UserEntity implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 4921460506552100743L;

    private String id;      //主键id
    private String userId;  //用户Id
    private String userStatus;      //用户状态
    private Date validTime;       //有效日期
    private String workId;      //员工工号
    private String remark;      //用户备注
    private String gender;     //性别
    private String userTime;    //用户时效

    public String getUserTime() {
        return userTime;
    }

    public void setUserTime(String userTime) {
        this.userTime = userTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }


    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
