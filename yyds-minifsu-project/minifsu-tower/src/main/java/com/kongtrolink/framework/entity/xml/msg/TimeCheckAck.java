package com.kongtrolink.framework.entity.xml.msg;

import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.entity.xml.base.Info;
import com.kongtrolink.framework.entity.xml.base.PKType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Info")
public class TimeCheckAck extends Info {
    @XmlElement(name = "Result")
    private int result;

    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.TIME_CHECK_ACK, CntbPktTypeTable.TIME_CHECK_ACK_CODE);
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
