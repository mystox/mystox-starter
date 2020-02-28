package com.kongtrolink.framework.scloud.entity;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2020/2/28 13:10
 * @Description:告警关注实体类
 */
public class AlarmFocus {

    private String id;
    private String alarmId;
    private Date focusTime;
    private String userId;
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public Date getFocusTime() {
        return focusTime;
    }

    public void setFocusTime(Date focusTime) {
        this.focusTime = focusTime;
    }

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
}
