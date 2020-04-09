package com.kongtrolink.framework.reports.entity.alarmHistory;

import java.util.Date;

/**
 * \* @Author: mystox
 * \* Date: 2020/4/8 16:44
 * \* Description:
 * \
 */
public class AlarmHistoryTemp {
    private String id;
    private String name;
    private String timePeriod;
    private Date createTime;
    private String uri;
    private String type; //周度 月度 季度 年度


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}