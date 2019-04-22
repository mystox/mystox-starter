package com.kongtrolink.framework.execute.module.model;

/**
 * @author fengw
 * 信号点信息
 * 新建文件 2019-4-18 20:13:45
 */
public class Signal {
    //所属内部设备类型
    private int type;
    //内部信号点Id
    private String signalId;
    //铁塔信号点Id
    private String cntbId;
    //信号点类型
    private int idType;
    //信号点名称
    private String name;
    //是否简易信号点(2G时是否上传)
    private boolean simpleEnable;
    //单位
    private String unit;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSimpleEnable() {
        return simpleEnable;
    }

    public void setSimpleEnable(boolean simpleEnable) {
        this.simpleEnable = simpleEnable;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
