package com.kongtrolink.framework.entity.xml.send;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Device {
    @XmlAttribute(name = "Id")
    private String Id;
    @XmlAttribute(name = "Code")
    private String Code;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
