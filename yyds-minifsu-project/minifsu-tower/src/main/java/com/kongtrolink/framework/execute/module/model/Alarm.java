package com.kongtrolink.framework.execute.module.model;

/**
 * @author fengw
 * 告警点信息
 * 新建文件 2019-4-18 20:14:44
 */
public class Alarm {
    //所属内部设备类型
    private int type;
    //告警点Id
    private String alarmId;
    //铁塔告警点Id
    private String cntbId;
    //告警等级
    private int level;
    //告警描述
    private String desc;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
