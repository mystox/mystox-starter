package com.kongtrolink.framework.gateway.tower.core.entity.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class TThreshold {
    @XmlAttribute(name = "Type")
    private int type;
    @XmlAttribute(name = "Id")
    private String Id;
    @XmlAttribute(name = "Threshold")
    private String threshold;
    @XmlAttribute(name = "AbsoluteVal")
    private String absoluteVal;
    @XmlAttribute(name = "RelativeVal")
    private String relativeVal;
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

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getAbsoluteVal() {
        return absoluteVal;
    }

    public void setAbsoluteVal(String absoluteVal) {
        this.absoluteVal = absoluteVal;
    }

    public String getRelativeVal() {
        return relativeVal;
    }

    public void setRelativeVal(String relativeVal) {
        this.relativeVal = relativeVal;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
