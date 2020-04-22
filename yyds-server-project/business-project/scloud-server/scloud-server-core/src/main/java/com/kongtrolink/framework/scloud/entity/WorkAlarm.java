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
    private Integer level;       //告警等级
    private String state;       //告警状态
    private Date treport;       //告警时间
    private Date trecover;      //告警清除时间
    private String table ;      //业务模块所在表

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getTrecover() {
        return trecover;
    }

    public void setTrecover(Date trecover) {
        this.trecover = trecover;
    }

    public Date getTreport() {

        return treport;
    }

    public void setTreport(Date treport) {
        this.treport = treport;
    }
}
