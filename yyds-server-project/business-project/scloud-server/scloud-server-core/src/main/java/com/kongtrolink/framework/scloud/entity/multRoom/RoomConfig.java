package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;

/**
 * 综合机房设置
 * by Mag on 6/20/2018.
 */
public class RoomConfig implements Serializable {

    private static final long serialVersionUID = 3753876218118962078L;

    private String id;
    private String siteId;
    private String userId;
    private int home;// 0-组态;1-星状拓扑图;2-组织拓扑图;3-监控视频;
    private int alarm = 7;//历史警告统计时间段(7-周 30-月 90-季 180-半年 365-年) 默认7
    private int alarmTop = 7;//TOP3历史警告统计时间段(7-周 30-月 90-季 180-半年 365-年)默认7

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public int getAlarmTop() {
        return alarmTop;
    }

    public void setAlarmTop(int alarmTop) {
        this.alarmTop = alarmTop;
    }
}
