package com.kongtrolink.framework.execute.module.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by mystoxlol on 2019/3/28, 15:40.
 * company: kongtrolink
 * description:
 * update record:
 */
@Document
public class TerminalProperties {
    @Id
    private String id;
    private String terminalId;
    private Integer business;
    private Integer accessMode;
    private String carrier;
    private String nwType;
    private String wmType;
    private String wmVendor;
    private String imsi;
    private String imei;
    private Integer signalStrength;
    private String engineVer;
    private String adapterVer;


    public Integer getBusiness() {
        return business;
    }

    public void setBusiness(Integer business) {
        this.business = business;
    }

    public Integer getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(Integer accessMode) {
        this.accessMode = accessMode;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getNwType() {
        return nwType;
    }

    public void setNwType(String nwType) {
        this.nwType = nwType;
    }

    public String getWmType() {
        return wmType;
    }

    public void setWmType(String wmType) {
        this.wmType = wmType;
    }

    public String getWmVendor() {
        return wmVendor;
    }

    public void setWmVendor(String wmVendor) {
        this.wmVendor = wmVendor;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    public String getEngineVer() {
        return engineVer;
    }

    public void setEngineVer(String engineVer) {
        this.engineVer = engineVer;
    }

    public String getAdapterVer() {
        return adapterVer;
    }

    public void setAdapterVer(String adapterVer) {
        this.adapterVer = adapterVer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}
