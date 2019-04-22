package com.kongtrolink.framework.entity.xml.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Value {
    @XmlElement(name = "DeviceList")
    private List<XmlList> deviceListList;
    @XmlElement(name = "TAlarmList")
    private List<XmlList> alarmListList;

    public Value() {
        this.deviceListList = new ArrayList<>();
        this.alarmListList = new ArrayList<>();
    }

    public List<XmlList> getDeviceListList() {
        return deviceListList;
    }

    public void setDeviceListList(List<XmlList> deviceListList) {
        this.deviceListList = deviceListList;
    }

    public List<XmlList> getAlarmListList() {
        return alarmListList;
    }

    public void setAlarmListList(List<XmlList> alarmListList) {
        this.alarmListList = alarmListList;
    }
}
