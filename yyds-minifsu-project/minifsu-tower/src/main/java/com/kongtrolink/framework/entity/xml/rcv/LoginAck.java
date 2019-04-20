package com.kongtrolink.framework.entity.xml.rcv;

import com.kongtrolink.framework.entity.xml.base.Info;
import com.kongtrolink.framework.entity.xml.base.PKType;

import javax.xml.bind.annotation.*;

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
        throw new UnsupportedOperationException("BaseFsuSetResp 不支持获取默认 PK_Type 的操作！"
                + "可在 SET_LOGININFO_ACK / SET_FTP_ACK / TIME_CHECK_ACK / "
                + "UPDATE_FSUINFO_INTERVAL_ACK / SET_FSUREBOOT_ACK 中选择");
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
