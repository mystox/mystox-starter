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
public class SetPoint extends Info {
    @XmlElement(name = "FsuId")
    private String fsuId;
    @XmlElement(name = "FsuCode")
    private String fsuCode;
    @XmlElement(name = "Value")
    private Value value;

    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.SET_POINT, CntbPktTypeTable.SET_POINT_CODE);
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }


}
