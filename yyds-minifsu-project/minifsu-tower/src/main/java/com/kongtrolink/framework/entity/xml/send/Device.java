package com.kongtrolink.framework.entity.xml.send;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Device {
    @XmlAttribute(name = "Id")
    private String Id;
    @XmlAttribute(name = "Code")
    private String Code;
    @XmlElement(name = "Id")
    private List<String> idList;
    @XmlElement(name = "TSemaphore")
    private List<TSemaphore> tSemaphoreList;
    @XmlElement(name = "TThreshold")
    private List<TThreshold> tThresholdList;
    @XmlElement(name = "SuccessList")
    private SetResultList successList;
    @XmlElement(name = "FailList")
    private SetResultList failList;

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

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public List<TSemaphore> gettSemaphoreList() {
        return tSemaphoreList;
    }

    public void settSemaphoreList(List<TSemaphore> tSemaphoreList) {
        this.tSemaphoreList = tSemaphoreList;
    }

    public List<TThreshold> gettThresholdList() {
        return tThresholdList;
    }

    public void settThresholdList(List<TThreshold> tThresholdList) {
        this.tThresholdList = tThresholdList;
    }

    public SetResultList getSuccessList() {
        return successList;
    }

    public void setSuccessList(SetResultList successList) {
        this.successList = successList;
    }

    public SetResultList getFailList() {
        return failList;
    }

    public void setFailList(SetResultList failList) {
        this.failList = failList;
    }
}
