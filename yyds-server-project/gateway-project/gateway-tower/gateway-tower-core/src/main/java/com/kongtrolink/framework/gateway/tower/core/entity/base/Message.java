/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kongtrolink.framework.gateway.tower.core.entity.base;


import com.kongtrolink.framework.gateway.tower.core.entity.msg.*;

import javax.xml.bind.annotation.*;
/**
 *
 * @author Mag
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "pkType" , "info" })
@XmlRootElement(name = "Request")
public class Message {
    
    @XmlElement(name = "PK_Type")
    protected PKType pkType;
    //这里存在冗余
    @XmlElements({
            @XmlElement(name = "Info", type = GetData.class),
            @XmlElement(name = "Info", type = SetPoint.class),
            @XmlElement(name = "Info", type = GetThreshold.class),
            @XmlElement(name = "Info", type = SetThreshold.class),
            @XmlElement(name = "Info", type = TimeCheck.class),
            @XmlElement(name = "Info", type = GetFsuInfo.class)
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
