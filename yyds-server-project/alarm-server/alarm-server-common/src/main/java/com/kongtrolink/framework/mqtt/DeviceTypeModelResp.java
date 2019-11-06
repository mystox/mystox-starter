package com.kongtrolink.framework.mqtt;

/**
 * @Auther: liudd
 * @Date: 2019/11/1 10:34
 * @Description: 根据enterpriserCode, serverCode获取所有设备类型，设备信号返回数据类型
 */
public class DeviceTypeModelResp {

    private String type;
    private String model;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
