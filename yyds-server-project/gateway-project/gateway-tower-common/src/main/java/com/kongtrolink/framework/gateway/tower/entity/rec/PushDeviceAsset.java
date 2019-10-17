package com.kongtrolink.framework.gateway.tower.entity.rec;

import com.kongtrolink.framework.gateway.tower.entity.rec.base.RecBase;
import com.kongtrolink.framework.gateway.tower.entity.rec.info.PushDeviceAssetDeviceList;

/**
 * IAIoT Edge向IAIoT Cloud推送设备资产信息
 * Created by Mag on 2019/10/14.
 */
public class PushDeviceAsset extends RecBase {

    private static final long serialVersionUID = -1442752946648214267L;

    private PushDeviceAssetDeviceList payload;

    public PushDeviceAssetDeviceList getPayload() {
        return payload;
    }

    public void setPayload(PushDeviceAssetDeviceList payload) {
        this.payload = payload;
    }
}
