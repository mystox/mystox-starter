package com.kongtrolink.framework.gateway.tower.entity.rec;

import com.kongtrolink.framework.gateway.tower.entity.rec.base.RecBase;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushRealtimeDataDeviceList;

/**
 * 上报变化数据
 * Created by Mag on 2019/10/14.
 */
public class PushRealtimeData extends RecBase {

    private static final long serialVersionUID = -8462036309129176534L;

    private PushRealtimeDataDeviceList payload;

    public PushRealtimeDataDeviceList getPayload() {
        return payload;
    }

    public void setPayload(PushRealtimeDataDeviceList payload) {
        this.payload = payload;
    }
}
