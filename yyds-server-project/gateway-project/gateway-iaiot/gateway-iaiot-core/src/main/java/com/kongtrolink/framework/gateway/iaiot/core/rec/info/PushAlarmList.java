package com.kongtrolink.framework.gateway.iaiot.core.rec.info;

import java.util.List;

/**
 * 告警详情
 * Created by Mag on 2019/10/14.
 */
public class PushAlarmList {

    private List<PushAlarmInfo> alarms;

    public List<PushAlarmInfo> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<PushAlarmInfo> alarms) {
        this.alarms = alarms;
    }
}
