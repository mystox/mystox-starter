package com.kongtrolink.framework.gateway.tower.entity.assent;

import java.io.Serializable;

/**
 * 3.6.1.6.2.1.	上报设备信息
 * Created by Mag on 2019/10/16.
 */
public class DeviceReportExtend implements Serializable {


    private static final long serialVersionUID = -5750717931107827997L;
    private String model;//需要一张类型映射的类型映射表，实现实际设备类型 -- 资管设备类型映射表

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
