package com.kongtrolink.framework.jsonType;

import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;

import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/3/29 13:38
 * @Description:
 */
public class JsonSignal {

    private String id;
    private float v;

    //JSON转换使用字段
    private List<AlarmSignalConfig> alarmSignals;
    //信号点id， 告警
    private Map<String, Alarm> alarmMap;

    private List<Alarm> alarmList;

    public List<Alarm> getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    public JsonSignal() {
    }

    public JsonSignal(String id, float v) {
        this.id = id;
        this.v = v;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public Map<String, Alarm> getAlarmMap() {
        return alarmMap;
    }

    public void setAlarmMap(Map<String, Alarm> alarmMap) {
        this.alarmMap = alarmMap;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
