package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;

/**
 * xx
 * by Mag on 6/21/2018.
 */
public class RoomSignalType implements Serializable,Comparable<RoomSignalType> {
    private static final long serialVersionUID = 577978484905362374L;
    private String cntbId;
    private String typeName;
    private String measurement;
    private String type;    //信号点类型。"0"-遥信信号(DI),"1"-遥测信号(AI),"2"-遥控信号(DO),"3"-遥调信号(AO)
    private int order;

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(RoomSignalType o) {
        return this.getOrder() - o.getOrder();
    }
}
