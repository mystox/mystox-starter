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
@XmlRootElement(name = "Response")
public class MessageResp {
    
    @XmlElement(name = "PK_Type")
    protected PKType pkType;
    @XmlElements({
            @XmlElement(name = "Info", type = LoginAck.class),
            @XmlElement(name = "Info", type = SendAlarmAck.class),
            @XmlElement(name = "Info", type = GetDataAck.class),
            @XmlElement(name = "Info", type = SetPointAck.class),
            @XmlElement(name = "Info", type = GetThresholdAck.class),
            @XmlElement(name = "Info", type = SetThresholdAck.class),
            @XmlElement(name = "Info", type = TimeCheckAck.class),
            @XmlElement(name = "Info", type = GetFsuInfoAck.class)
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
