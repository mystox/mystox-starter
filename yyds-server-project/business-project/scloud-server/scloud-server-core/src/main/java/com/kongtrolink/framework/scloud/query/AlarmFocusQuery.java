package com.kongtrolink.framework.scloud.query;

/**
 * @Auther: liudd
 * @Date: 2020/2/28 13:11
 * @Description:
 */
public class AlarmFocusQuery extends Paging{

    private String beginTime;
    private String endTiem;
    private String userId;

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTiem() {
        return endTiem;
    }

    public void setEndTiem(String endTiem) {
        this.endTiem = endTiem;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
