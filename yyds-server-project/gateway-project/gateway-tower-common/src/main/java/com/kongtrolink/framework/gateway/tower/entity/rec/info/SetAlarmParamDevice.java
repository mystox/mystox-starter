package com.kongtrolink.framework.gateway.tower.entity.rec.info;

import com.kongtrolink.framework.gateway.tower.entity.send.info.BaseId;

import java.util.List;

/**
 * 设置告警参数 回复设备
 * Created by Mag on 2019/10/14.
 */
public class SetAlarmParamDevice {
    private String id;
    private List<BaseId> alarmPoints;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BaseId> getAlarmPoints() {
        return alarmPoints;
    }

    public void setAlarmPoints(List<BaseId> alarmPoints) {
        this.alarmPoints = alarmPoints;
    }
}
