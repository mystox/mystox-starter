package com.kongtrolink.framework.scloud.entity;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2020/4/2 09:54
 * @Description:
 */
public class WorkAlarm {

    private String alarmKey;     //告警Key，告警产生过程中，还没入库，不存在id
    private String alarmName;   //告警名称
    private String level;       //告警等级
    private String state;       //告警状态
    private Date tReport;       //告警时间
    private Date tRecover;      //告警清除时间

    public String getAlarmKey() {
        return alarmKey;
    }

    public void setAlarmKey(String alarmKey) {
        this.alarmKey = alarmKey;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date gettReport() {
        return tReport;
    }

    public void settReport(Date tReport) {
        this.tReport = tReport;
    }

    public Date gettRecover() {
        return tRecover;
    }

    public void settRecover(Date tRecover) {
        this.tRecover = tRecover;
    }

    /**
     * 告警转换成WorkAlarm
     * @param alarm
     * @return
     */
    public static WorkAlarm AlarmToWorkAlarm(Alarm alarm){
        WorkAlarm workAlarm = new WorkAlarm();
        workAlarm.setAlarmKey(alarm.initKey());
        workAlarm.setAlarmName(alarm.getName());
        workAlarm.setLevel(alarm.getTargetLevelName());
        workAlarm.setState(alarm.getState());
        workAlarm.settReport(alarm.getTreport());
        workAlarm.settRecover(alarm.getTrecover());
        return workAlarm;
    }
}
