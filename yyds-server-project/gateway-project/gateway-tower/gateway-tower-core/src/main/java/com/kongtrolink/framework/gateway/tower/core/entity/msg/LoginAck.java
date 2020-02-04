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
public class LoginAck extends Info {

    @XmlElement(name = "SCIP")
    protected String scIp;
    @XmlElement(name = "RightLevel")
    protected String rightLevel;

    public LoginAck() {
    }


    public LoginAck(String scIp, String rightLevel) {
        this.scIp = scIp;
        this.rightLevel = rightLevel;
    }

    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.LOGIN_ACK, CntbPktTypeTable.LOGIN_ACK_CODE);
    }

    public String getScIp() {
        return scIp;
    }

    public void setScIp(String scIp) {
        this.scIp = scIp;
    }

    public String getRightLevel() {
        return rightLevel;
    }

    public void setRightLevel(String rightLevel) {
        this.rightLevel = rightLevel;
    }


}
