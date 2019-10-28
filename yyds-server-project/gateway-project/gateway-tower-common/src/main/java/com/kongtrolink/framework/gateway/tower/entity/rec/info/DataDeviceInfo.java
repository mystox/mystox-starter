package com.kongtrolink.framework.gateway.tower.entity.rec.info;

import java.util.List;

/**
 * 设备数据模型信息
 * Created by Mag on 2019/10/14.
 */
public class DataDeviceInfo {

    private String id;//设备ID
    private int type;//	number	否	设备类型
    private String model;//		string	否	设备型号
    private int resourceNo;//		number	否	设备资源编号
    private List<DataPoint>  dataPoints;//		array	是	数据点列表

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(int resourceNo) {
        this.resourceNo = resourceNo;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
