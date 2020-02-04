package com.kongtrolink.framework.gateway.iaiot.core.rec;


import com.kongtrolink.framework.gateway.iaiot.core.rec.info.SetAlarmParamAckDeviceList;

/**
 * 设置告警参数 响应
 * Created by Mag on 2019/10/14.
 */
public class SetAlarmParamAck extends RecBase {
    private static final long serialVersionUID = 7790678685056852462L;

    private SetAlarmParamAckDeviceList payload;

    public SetAlarmParamAckDeviceList getPayload() {
        return payload;
    }

    public void setPayload(SetAlarmParamAckDeviceList payload) {
        this.payload = payload;
    }
}
