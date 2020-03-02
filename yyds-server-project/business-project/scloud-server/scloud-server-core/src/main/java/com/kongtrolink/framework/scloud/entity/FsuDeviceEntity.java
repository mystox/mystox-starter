package com.kongtrolink.framework.scloud.entity;

import java.util.List;

/**
 * FSU与其他设备的绑定关系 数据实体类
 * Created by Eric on 2020/2/12.
 */
public class FsuDeviceEntity {

    private String fsuCode;   //FSU Code,即保存在device表中FSU动环主机设备对应的code字段
    private String deviceCode;  //设备编码

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCodes) {
        this.deviceCode = deviceCodes;
    }
}
