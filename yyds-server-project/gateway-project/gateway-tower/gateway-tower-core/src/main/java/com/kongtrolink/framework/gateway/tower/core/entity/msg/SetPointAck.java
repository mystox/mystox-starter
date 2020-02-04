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
public class SetPointAck extends Info {

    @XmlElement(name = "FsuId")
    private String fsuId;
    @XmlElement(name = "FsuCode")
    private String fsuCode;
    @XmlElement(name = "Result")
    private int result;
    @XmlElement(name = "DeviceList")
    private SetDeviceList deviceList ;

    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.SET_POINT_ACK, CntbPktTypeTable.SET_POINT_ACK_CODE);
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

    public SetDeviceList getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(SetDeviceList deviceList) {
        this.deviceList = deviceList;
    }
}
