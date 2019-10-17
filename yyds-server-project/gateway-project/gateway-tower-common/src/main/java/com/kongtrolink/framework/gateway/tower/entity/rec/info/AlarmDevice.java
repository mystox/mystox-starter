package com.kongtrolink.framework.gateway.tower.entity.rec.info;

import java.util.List;

/**
 * 告警点信息
 * Created by Mag on 2019/10/14.
 */
public class AlarmDevice {

    private String id;//	ID

    private List<AlarmPoint> alarmPoints;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AlarmPoint> getAlarmPoints() {
        return alarmPoints;
    }

    public void setAlarmPoints(List<AlarmPoint> alarmPoints) {
        this.alarmPoints = alarmPoints;
    }
}
