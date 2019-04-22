package com.kongtrolink.framework.entity.xml.rcv;

import com.kongtrolink.framework.entity.xml.send.DeviceList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Value {
    @XmlElement(name = "DeviceList")
    private List<DeviceList> deviceListList;

    public Value() {
        this.deviceListList = new ArrayList<>();
    }

    public List<DeviceList> getDeviceListList() {
        return deviceListList;
    }

    public void setDeviceListList(List<DeviceList> deviceListList) {
        this.deviceListList = deviceListList;
    }
}
