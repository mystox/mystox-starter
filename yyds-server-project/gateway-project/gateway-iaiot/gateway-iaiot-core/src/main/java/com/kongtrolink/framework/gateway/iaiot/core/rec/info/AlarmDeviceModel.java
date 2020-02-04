package com.kongtrolink.framework.gateway.iaiot.core.rec.info;

import java.util.List;

/**
 * 告警点信息
 * Created by Mag on 2019/10/14.
 */
public class AlarmDeviceModel {

    private String id;
    private int type;
    private String model;
    private int resourceNo;
    private List<AlarmPointSimple> alarmPoints;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(int resourceNo) {
        this.resourceNo = resourceNo;
    }

    public List<AlarmPointSimple> getAlarmPoints() {
        return alarmPoints;
    }

    public void setAlarmPoints(List<AlarmPointSimple> alarmPoints) {
        this.alarmPoints = alarmPoints;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
