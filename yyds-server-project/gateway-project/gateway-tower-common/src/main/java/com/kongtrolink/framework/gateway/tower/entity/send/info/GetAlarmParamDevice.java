package com.kongtrolink.framework.gateway.tower.entity.send.info;

import java.util.List;

/**
 * 获取告警参数
 * Created by Mag on 2019/10/14.
 */
public class GetAlarmParamDevice {

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
