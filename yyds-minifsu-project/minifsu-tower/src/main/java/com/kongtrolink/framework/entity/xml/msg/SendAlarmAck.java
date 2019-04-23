package com.kongtrolink.framework.entity.xml.msg;

import com.kongtrolink.framework.entity.xml.base.Info;
import com.kongtrolink.framework.entity.xml.base.PKType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Info")
public class SendAlarmAck extends Info {

    @XmlElement(name = "Result")
    protected int result;

    public SendAlarmAck() {}

    @Override
    public PKType pkType() {
        throw new UnsupportedOperationException("BaseFsuSetResp 不支持获取默认 PK_Type 的操作！"
                + "可在 SET_LOGININFO_ACK / SET_FTP_ACK / TIME_CHECK_ACK / "
                + "UPDATE_FSUINFO_INTERVAL_ACK / SET_FSUREBOOT_ACK 中选择");
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
