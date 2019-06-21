package com.kongtrolink.framework.execute.module.model;

/**
 * 设备类型对照表
 */
public class DevType {
    //内部设备类型
    private int type;
    //铁塔设备类型
    private String cntbType;
    //铁塔设备名称
    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCntbType() {
        return cntbType;
    }

    public void setCntbType(String cntbType) {
        this.cntbType = cntbType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
