package com.kongtrolink.framework.entity.xml.rcv;

import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.entity.xml.base.Info;
import com.kongtrolink.framework.entity.xml.base.PKType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "fsuId" , "fsuCode"})
@XmlRootElement(name = "Info")
public class GetFsuInfo extends Info {

    @XmlElement(name = "FsuId")
    public String fsuId;
    @XmlElement(name = "FsuCode")
    public String fsuCode;      // 需采用 MD5 进行加密


    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.GET_FSUINFO, CntbPktTypeTable.GET_FSUINFO_CODE);
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
}

