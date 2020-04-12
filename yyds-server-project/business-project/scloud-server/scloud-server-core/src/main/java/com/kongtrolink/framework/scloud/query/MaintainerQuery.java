package com.kongtrolink.framework.scloud.query;

import java.util.List;

/**
 * 维护用户 查询类
 * Created by Eric on 2020/2/28.
 */
public class MaintainerQuery extends Paging{
    /**
     *
     */
    private static final long serialVersionUID = 7450611351413631084L;
    private String userId;  //用户Id
    private List<String> userIds;   //用户Id（集合）
    private String username;    //账号
    private String name;    //姓名
    private String companyName; //代维公司
    private String currentPostId;   //角色Id
    private String currentPositionName; //角色名称

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
