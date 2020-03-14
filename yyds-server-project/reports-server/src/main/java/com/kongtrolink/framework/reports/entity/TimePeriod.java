package com.kongtrolink.framework.reports.entity;

import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
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
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public Date getStartTime() {
        return startTime;
    }

    public String getStartTimeStr() {
        return format.format(startTime);
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getEndTimeStr() {
        return format.format(endTime);
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "TimePeriod{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public static void main(String[] args)
    {
        JSONObject time = new JSONObject();
        time.put("startTime", "2020-01-01");
        time.put("endTime", "2020-01-11");

        TimePeriod timePeriod = time.toJavaObject(TimePeriod.class);
        System.out.println(timePeriod);
        System.out.println();

    }
}