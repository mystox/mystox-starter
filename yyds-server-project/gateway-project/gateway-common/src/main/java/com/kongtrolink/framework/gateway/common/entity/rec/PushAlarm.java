package com.kongtrolink.framework.gateway.common.entity.rec;

import com.kongtrolink.framework.gateway.common.entity.rec.base.RecBase;
import com.kongtrolink.framework.gateway.common.entity.rec.info.PushAlarmList;

/**
 * 上报告警
 * Created by Mag on 2019/10/14.
 */
public class PushAlarm extends RecBase {

    private static final long serialVersionUID = 4225274467049473780L;

    private PushAlarmList payload;

    public PushAlarmList getPayload() {
        return payload;
    }

    public void setPayload(PushAlarmList payload) {
        this.payload = payload;
    }
}
