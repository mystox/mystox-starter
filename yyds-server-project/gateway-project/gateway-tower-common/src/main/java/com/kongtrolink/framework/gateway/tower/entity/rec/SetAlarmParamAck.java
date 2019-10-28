package com.kongtrolink.framework.gateway.tower.entity.rec;

import com.kongtrolink.framework.gateway.tower.entity.rec.base.RecBase;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.SetAlarmParamDeviceList;

/**
 * 设置告警参数 响应
 * Created by Mag on 2019/10/14.
 */
public class SetAlarmParamAck extends RecBase{
    private static final long serialVersionUID = 7790678685056852462L;

    private SetAlarmParamDeviceList payload;

    public SetAlarmParamDeviceList getPayload() {
        return payload;
    }

    public void setPayload(SetAlarmParamDeviceList payload) {
        this.payload = payload;
    }
}
