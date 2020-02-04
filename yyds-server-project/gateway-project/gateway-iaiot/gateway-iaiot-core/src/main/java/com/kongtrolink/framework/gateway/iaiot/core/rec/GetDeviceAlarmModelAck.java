package com.kongtrolink.framework.gateway.iaiot.core.rec;

import com.kongtrolink.framework.gateway.iaiot.core.rec.info.AlarmDeviceModelList;

/**
 * 获取设备告警模型信息
 * Created by Mag on 2019/10/14.
 */
public class GetDeviceAlarmModelAck  extends RecBase {

    private AlarmDeviceModelList payload;

    public AlarmDeviceModelList getPayload() {
        return payload;
    }

    public void setPayload(AlarmDeviceModelList payload) {
        this.payload = payload;
    }
}
