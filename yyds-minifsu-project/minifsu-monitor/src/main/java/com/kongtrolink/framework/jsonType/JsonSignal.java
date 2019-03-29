package com.kongtrolink.framework.jsonType;

import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignal;

import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/3/29 13:38
 * @Description:
 */
public class JsonSignal {

    private String id;
    private Double v;

    //JSON转换使用字段
    private List<AlarmSignal> alarmSignals;
    //信号点id， 告警
    private Map<String, Alarm> alarmMap;

    public Map<String, Alarm> getAlarmMap() {
        return alarmMap;
    }

    public void setAlarmMap(Map<String, Alarm> alarmMap) {
        this.alarmMap = alarmMap;
    }

    public Double getV() {
        return v;
    }

    public void setV(Double v) {
        this.v = v;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AlarmSignal> getAlarmSignals() {
        return alarmSignals;
    }

    public void setAlarmSignals(List<AlarmSignal> alarmSignals) {
        this.alarmSignals = alarmSignals;
    }
}
