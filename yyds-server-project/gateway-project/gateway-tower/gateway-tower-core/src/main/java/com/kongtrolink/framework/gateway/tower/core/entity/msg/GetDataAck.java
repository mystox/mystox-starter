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
public class GetDataAck extends Info {
    @XmlElement(name = "FsuId")
    private String fsuId;
    @XmlElement(name = "FsuCode")
    private String fsuCode;
    @XmlElement(name = "Values")
    private Value value;
    @XmlElement(name = "Result")
    private int result;

    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.GET_DATA_ACK, CntbPktTypeTable.GET_DATA_ACK_CODE);
    }

    public GetDataAck() {
        this.value = new Value();
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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
