package com.kongtrolink.framework.entity.xml.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class XmlList {

    @XmlElement(name = "Device")
    private List<Device> deviceList;
    @XmlElement(name = "TAlarm")
    private List<TAlarm> tAlarmList;

    public XmlList() {
        this.deviceList = new ArrayList<>();
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public List<TAlarm> gettAlarmList() {
        return tAlarmList;
    }

    public void settAlarmList(List<TAlarm> tAlarmList) {
        this.tAlarmList = tAlarmList;
    }
}
