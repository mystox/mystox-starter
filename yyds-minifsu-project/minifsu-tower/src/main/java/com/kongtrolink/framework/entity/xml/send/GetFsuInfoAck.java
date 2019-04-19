package com.kongtrolink.framework.entity.xml.send;

import com.kongtrolink.framework.entity.xml.base.Info;
import com.kongtrolink.framework.entity.xml.base.PKType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "fsuId" , "fsuCode","cpu","memu","result"})
@XmlRootElement(name = "Info")
public class GetFsuInfoAck extends Info {

    @XmlElement(name = "FsuId")
    public String fsuId;
    @XmlElement(name = "FsuCode")
    public String fsuCode;      // 需采用 MD5 进行加密
    @XmlElement(name = "CPUUsage")
    public String cpu;      // 需采用 MD5 进行加密
    @XmlElement(name = "MEMUsage")
    public String memu;      // 需采用 MD5 进行加密
    @XmlElement(name = "Result")
    public Integer result;      // 需采用 MD5 进行加密


    @Override
    public PKType pkType() {
        return new PKType("GET_FSUINFO_ACK",1702);
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMemu() {
        return memu;
    }

    public void setMemu(String memu) {
        this.memu = memu;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
