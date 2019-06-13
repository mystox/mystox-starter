package com.kongtrolink.framework.entity.xml.msg;

import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.entity.xml.base.Info;
import com.kongtrolink.framework.entity.xml.base.PKType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Info")
public class SetThresholdAck extends Info {

    @XmlElement(name = "FsuId")
    private String fsuId;
    @XmlElement(name = "FsuCode")
    private String fsuCode;
    @XmlElement(name = "Result")
    private int result;
    @XmlElement(name = "DeviceList")
    private List<Device> deviceList;

    public SetThresholdAck() {
        deviceList = new ArrayList<>();
    }

    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.SET_THRESHOLD_ACK, CntbPktTypeTable.SET_THRESHOLD_ACK_CODE);
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }
}
