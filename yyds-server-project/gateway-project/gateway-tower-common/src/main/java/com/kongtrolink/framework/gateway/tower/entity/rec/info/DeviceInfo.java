package com.kongtrolink.framework.gateway.tower.entity.rec.info;

/**
 * 设备信息
 * Created by Mag on 2019/10/14.
 */
public class DeviceInfo {

    private String id;//设备ID
    private int type;//	number	否	设备类型
    private int resourceNo;//		number	否	设备资源编号

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


    public int getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(int resourceNo) {
        this.resourceNo = resourceNo;
    }
}
