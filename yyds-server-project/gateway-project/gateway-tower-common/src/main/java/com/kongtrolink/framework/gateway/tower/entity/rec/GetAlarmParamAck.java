package com.kongtrolink.framework.gateway.tower.entity.rec;

import com.kongtrolink.framework.gateway.tower.entity.rec.base.RecBase;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.AlarmDeviceList;

/**
 * 获取告警参数
 * Created by Mag on 2019/10/14.
 */
public class GetAlarmParamAck  extends RecBase {

    private AlarmDeviceList payload;

    public AlarmDeviceList getPayload() {
        return payload;
    }

    public void setPayload(AlarmDeviceList payload) {
        this.payload = payload;
    }
}
