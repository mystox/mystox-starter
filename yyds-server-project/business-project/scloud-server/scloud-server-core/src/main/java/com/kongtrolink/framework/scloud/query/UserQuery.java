package com.kongtrolink.framework.scloud.query;

import java.util.List;

/**
 * 系统用户 查询类
 * Created by Eric on 2020/2/28.
 */
public class UserQuery extends Paging{
    /**
     *
     */
    private static final long serialVersionUID = -5591511152856559674L;

    private String userId;  //用户Id
    private List<String> userIds;   //用户Id列表
    private String name;        //姓名
    private String username;        //账号
    private String currentPostId;   //角色id
    private String currentRoleName;     //角色名称
    private String lockStatus;      //锁定状态
    private String userStatus;      //用户状态
    private Long validTime;       //有效日期
    private Long startTime;     //开始时间
    private Long endTime;       //结束时间
    private String userTime;

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getUserId() {
        return userId;
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

    public String getCurrentPostId() {
        return currentPostId;
    }

    public void setCurrentPostId(String currentPostId) {
        this.currentPostId = currentPostId;
    }

    public String getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(String lockStatus) {
        this.lockStatus = lockStatus;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public Long getValidTime() {
        return validTime;
    }

    public void setValidTime(Long validTime) {
        this.validTime = validTime;
    }
}
