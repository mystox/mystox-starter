package com.kongtrolink.framework.scloud.entity;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/14 16:22
 * @Description:
 */
public class AlarmSiteStatistics {

    private String siteCode;
    private String siteName;
    private String timeStr;
    private int count;
    private List<String> properties;
    private List<String> values;

//    private List<AlarmSiteCount> alarmSiteCountList;

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }


    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
//
//    public List<AlarmSiteCount> getAlarmSiteCountList() {
//        return alarmSiteCountList;
//    }
//
//    public void setAlarmSiteCountList(List<AlarmSiteCount> alarmSiteCountList) {
//        this.alarmSiteCountList = alarmSiteCountList;
//    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
