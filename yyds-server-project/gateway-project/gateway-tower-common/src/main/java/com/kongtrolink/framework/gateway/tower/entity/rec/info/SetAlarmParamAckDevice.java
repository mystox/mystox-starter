package com.kongtrolink.framework.gateway.tower.entity.rec.info;

import java.util.List;

/**
 * 设置告警参数 回复设备
 * Created by Mag on 2019/10/14.
 */
public class SetAlarmParamAckDevice {
    private String id;
    private List<DataPointResult> alarmPoints;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DataPointResult> getAlarmPoints() {
        return alarmPoints;
    }

    public void setAlarmPoints(List<DataPointResult> alarmPoints) {
        this.alarmPoints = alarmPoints;
    }
}
