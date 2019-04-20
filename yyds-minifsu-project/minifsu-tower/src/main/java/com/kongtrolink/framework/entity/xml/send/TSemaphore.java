package com.kongtrolink.framework.entity.xml.send;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class TSemaphore {
    @XmlAttribute(name = "Type")
    private int type;
    @XmlAttribute(name = "Id")
    private String Id;
    @XmlAttribute(name = "MeasuredVal")
    private String measuredVal;
    @XmlAttribute(name = "SetupVal")
    private String setupVal;
    @XmlAttribute(name = "Status")
    private int status;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMeasuredVal() {
        return measuredVal;
    }

    public void setMeasuredVal(String measuredVal) {
        this.measuredVal = measuredVal;
    }

    public String getSetupVal() {
        return setupVal;
    }

    public void setSetupVal(String setupVal) {
        this.setupVal = setupVal;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
