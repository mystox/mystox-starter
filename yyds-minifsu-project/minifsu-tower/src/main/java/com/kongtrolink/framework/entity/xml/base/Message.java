package com.kongtrolink.framework.entity.xml.base;

import com.kongtrolink.framework.entity.xml.send.Login;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "pkType" , "info" })
@XmlRootElement(name = "Request")
public class Message {

    @XmlElement(name = "PK_Type")
    protected PKType pkType;
    //这里存在冗余
    @XmlElements({
            @XmlElement(name = "Info", type = Login.class),          // LOGIN
    })
    protected Info info;

    public Message() {
    }

    public Message(Info info) {
        this.info = info;
        this.pkType = info.pkType();
    }

    public Message(Info info, PKType pkType) {
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