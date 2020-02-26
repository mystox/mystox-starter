package com.kongtrolink.framework.scloud.entity;

import java.util.List;

/**
 * FSU与其他设备的绑定关系 数据实体类
 * Created by Eric on 2020/2/12.
 */
public class FsuDeviceEntity {

    private String fsuCode;   //FSU Code,即保存在device表中FSU动环主机设备对应的code字段
    private List<String> deviceCodes;  //设备编码

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public List<String> getDeviceCodes() {
        return deviceCodes;
    }

    public void setDeviceCodes(List<String> deviceCodes) {
        this.deviceCodes = deviceCodes;
    }
}
