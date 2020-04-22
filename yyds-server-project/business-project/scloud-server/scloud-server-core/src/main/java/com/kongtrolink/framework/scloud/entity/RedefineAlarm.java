package com.kongtrolink.framework.scloud.entity;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 14:52
 * @Description:
 */
public class RedefineAlarm {

    private String id;
    private String ruleId;
    private String ruleName;
    private String alarmKey;
    private String alarmName;
    private String siteName;
    private int sourceLevel;
    private int targetLevel;
    private Date useTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getSourceLevel() {
        return sourceLevel;
    }

    public void setSourceLevel(int sourceLevel) {
        this.sourceLevel = sourceLevel;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(int targetLevel) {
        this.targetLevel = targetLevel;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public String getAlarmKey() {
        return alarmKey;
    }

    public void setAlarmKey(String alarmKey) {
        this.alarmKey = alarmKey;
    }

    public static RedefineAlarm init(RedefineRule redefineRule, AlarmBusiness alarmBusiness){
        RedefineAlarm redefineAlarm = new RedefineAlarm();
        redefineAlarm.setRuleId(redefineRule.getId());
        redefineAlarm.setRuleName(redefineRule.getName());
        redefineAlarm.setAlarmKey(alarmBusiness.getKey());
        redefineAlarm.setAlarmName(alarmBusiness.getName());
        redefineAlarm.setSiteName(alarmBusiness.getSiteName());
        redefineAlarm.setSourceLevel(alarmBusiness.getLevel());
        redefineAlarm.setTargetLevel(redefineRule.getAlarmLevel());
        redefineAlarm.setUseTime(redefineRule.getUpdateTime());
        return redefineAlarm;
    }
}
