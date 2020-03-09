package com.kongtrolink.framework.scloud.entity.multRoom;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2018/11/12.
 */
public class RoomSignalTypeDevice implements Serializable{

    private static final long serialVersionUID = -6282190906871928409L;

    private String cntbId;
    private String signalName;
    private String measurement;
    private int order;

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
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
}
