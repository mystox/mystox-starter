package com.kongtrolink.query;

import com.kongtrolink.enttiy.Alarm;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:44
 * @Description:
 */
public class AlarmQuery extends Alarm {

    private Date beginTime;
    private Date endTime;

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
