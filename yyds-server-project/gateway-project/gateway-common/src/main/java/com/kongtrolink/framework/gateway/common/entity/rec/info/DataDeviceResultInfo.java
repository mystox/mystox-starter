package com.kongtrolink.framework.gateway.common.entity.rec.info;

import java.util.List;

/**
 * 设备数据模型信息
 * Created by Mag on 2019/10/14.
 */
public class DataDeviceResultInfo {

    private String id;//设备ID
    private List<DataPointResult>  dataPoints;//		array	是	数据点列表

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DataPointResult> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPointResult> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
