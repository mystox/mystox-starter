package com.kongtrolink.framework.entity.xml.base;

import com.kongtrolink.framework.entity.xml.send.GetFsuInfoAck;
import com.kongtrolink.framework.entity.xml.send.Login;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "pkType" , "info" })
@XmlRootElement(name = "Response")
public class MessageResp {

    @XmlElement(name = "PK_Type")
    protected PKType pkType;
    @XmlElements({
            @XmlElement(name = "Info", type = Login.class),          // LOGIN
            @XmlElement(name = "Info", type = GetFsuInfoAck.class),
    })
    protected Info info;

    public MessageResp() {
    }

    public MessageResp(Info info) {
        this.info = info;
        this.pkType = info.pkType();
    }

    public MessageResp(Info info, PKType pkType) {
        this.info = info;
        this.pkType = pkType;
    }

    public PKType getPkType() {
        return pkType;
    }

    public void setPkType(PKType pkType) {
        this.pkType = pkType;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "BIntfMessage{" + "pkType=" + pkType + ", info=" + info + '}';
    }

}
