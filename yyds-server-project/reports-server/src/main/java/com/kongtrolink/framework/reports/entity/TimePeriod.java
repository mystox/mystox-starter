package com.kongtrolink.framework.reports.entity;

import java.util.Date;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/24 15:04
 * \* Description:
 * \
 */
public class TimePeriod {
    private Date startTime;
    private Date endTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}