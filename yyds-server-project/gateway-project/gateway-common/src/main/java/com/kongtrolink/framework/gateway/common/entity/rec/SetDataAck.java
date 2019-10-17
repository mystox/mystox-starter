package com.kongtrolink.framework.gateway.common.entity.rec;

import com.kongtrolink.framework.gateway.common.entity.rec.base.RecBase;
import com.kongtrolink.framework.gateway.common.entity.rec.info.SetDataAckInfo;

/**
 * 设置数据点数据 相应
 * Created by Mag on 2019/10/14.
 */
public class SetDataAck  extends RecBase {

    private SetDataAckInfo payload;

    public SetDataAckInfo getPayload() {
        return payload;
    }

    public void setPayload(SetDataAckInfo payload) {
        this.payload = payload;
    }
}
