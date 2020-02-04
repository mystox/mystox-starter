package com.kongtrolink.framework.gateway.tower.core.entity.msg;

import com.kongtrolink.framework.gateway.tower.core.entity.base.CntbPktTypeTable;
import com.kongtrolink.framework.gateway.tower.core.entity.base.Info;
import com.kongtrolink.framework.gateway.tower.core.entity.base.PKType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Info")
public class GetData extends Info {

    @XmlElement(name = "FsuId")
    private String fsuId;
    @XmlElement(name = "FsuCode")
    private String fsuCode;
    @XmlElement(name = "DeviceList")
    private XmlList deviceList;

    public GetData() {
        deviceList = new XmlList();
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

    public XmlList getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(XmlList deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.GET_DATA, CntbPktTypeTable.GET_DATA_CODE);
    }
}
