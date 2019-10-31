package com.kongtrolink.framework.gateway.tower.entity.rec;

import com.kongtrolink.framework.gateway.tower.entity.rec.base.RecBase;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.DataDevicesInfo;

/**
 * 获取设备数据模型信息 回复
 * Created by Mag on 2019/10/14.
 */
public class GetDeviceDataModelAck  extends RecBase {

    private static final long serialVersionUID = -6972823394576832986L;

    private DataDevicesInfo payload;

    public DataDevicesInfo getPayload() {
        return payload;
    }

    public void setPayload(DataDevicesInfo payload) {
        this.payload = payload;
    }
}
