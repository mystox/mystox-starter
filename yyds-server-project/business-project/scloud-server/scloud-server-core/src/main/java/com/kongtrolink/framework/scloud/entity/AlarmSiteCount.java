package com.kongtrolink.framework.scloud.entity;

/**
 * @Auther: liudd
 * @Date: 2020/4/14 16:40
 * @Description:
 */
public class AlarmSiteCount {

    private String timeStr;
    private int count;

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static AlarmSiteCount createByStatistics(AlarmSiteStatistics alarmSiteStatistics){
        AlarmSiteCount alarmSiteCount = new AlarmSiteCount();
        alarmSiteCount.setCount(alarmSiteStatistics.getCount());
        alarmSiteCount.setTimeStr(alarmSiteStatistics.getTimeStr());
        return alarmSiteCount;
    }
}
