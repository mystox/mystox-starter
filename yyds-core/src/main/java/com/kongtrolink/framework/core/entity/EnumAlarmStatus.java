package com.kongtrolink.framework.core.entity;

/**
 * @Auther: liudd
 * @Date: 2019/4/3 08:55
 * @Description:告警状态
 */
public enum  EnumAlarmStatus {

    BEGIN(1, "产生"),
    BEGINREPORT(2, "产生上报"),
    END(4, "结束"),
    ENDREPORT(8, "结束上报"),
    DEBINDELAY(16, "产生延迟"),
    REALBEGIN(32, "真实产生"),
    ENDDELAY(64, "消除延迟"),
    REALEND(128, "真实消除");

    private int value;
    private String name;

    EnumAlarmStatus(int value, String name){
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnumAlarmStatus getByValue(int value){
        for(EnumAlarmStatus alarmStatus : EnumAlarmStatus.values()){
            if(alarmStatus.getValue() == value){
                return alarmStatus;
            }
        }
        return null;
    }
}
