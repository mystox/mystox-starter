package com.kongtrolink.framework.entity.xml.send;

import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.entity.xml.base.Info;
import com.kongtrolink.framework.entity.xml.base.PKType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Info")
public class Login extends Info {

    @XmlElement(name = "UserName")
    private String userName;
    @XmlElement(name = "PaSCword")
    private String paSCWord;
    @XmlElement(name = "FsuId")
    private String fsuId;
    @XmlElement(name = "FsuCode")
    public String fsuCode;
    @XmlElement(name = "FsuIP")
    private String ip;
    @XmlElement(name = "MacId")
    private String macId;
    @XmlElement(name = "ImsiId")
    private String imsiId;
    @XmlElement(name = "NetworkType")
    private String networkType;
    @XmlElement(name = "LockedNetworkType")
    private String lockedNetworkType;
    @XmlElement(name = "Carrier")
    private String carrier;
    @XmlElement(name = "NMVendor")
    private String nmVendor;
    @XmlElement(name = "NMType")
    private String nmType;
    @XmlElement(name = "Reg_Mode")
    private int regMode;
    @XmlElement(name = "FSUVendor")
    private String fsuVendor;
    @XmlElement(name = "FSUType")
    private String fsuType;
    @XmlElement(name = "FSUClass")
    private String fsuClass;
    @XmlElement(name = "Vervion")
    private String version;
    @XmlElement(name = "DictVersion")
    private int dictVersion;
    @XmlElement(name = "DeviceList")
    private DeviceList deviceList;

    public Login() {
        deviceList = new DeviceList();
    }

    @Override
    public PKType pkType() {
        return new PKType(CntbPktTypeTable.LOGIN, CntbPktTypeTable.LOGIN_CODE);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPaSCWord() {
        return paSCWord;
    }

    public void setPaSCWord(String paSCWord) {
        this.paSCWord = paSCWord;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getImsiId() {
        return imsiId;
    }

    public void setImsiId(String imsiId) {
        this.imsiId = imsiId;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getLockedNetworkType() {
        return lockedNetworkType;
    }

    public void setLockedNetworkType(String lockedNetworkType) {
        this.lockedNetworkType = lockedNetworkType;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getNmVendor() {
        return nmVendor;
    }

    public void setNmVendor(String nmVendor) {
        this.nmVendor = nmVendor;
    }

    public String getNmType() {
        return nmType;
    }

    public void setNmType(String nmType) {
        this.nmType = nmType;
    }

    public int getRegMode() {
        return regMode;
    }

    public void setRegMode(int regMode) {
        this.regMode = regMode;
    }

    public String getFsuVendor() {
        return fsuVendor;
    }

    public void setFsuVendor(String fsuVendor) {
        this.fsuVendor = fsuVendor;
    }

    public String getFsuType() {
        return fsuType;
    }

    public void setFsuType(String fsuType) {
        this.fsuType = fsuType;
    }

    public String getFsuClass() {
        return fsuClass;
    }

    public void setFsuClass(String fsuClass) {
        this.fsuClass = fsuClass;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getDictVersion() {
        return dictVersion;
    }

    public void setDictVersion(int dictVersion) {
        this.dictVersion = dictVersion;
    }

    public DeviceList getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(DeviceList deviceList) {
        this.deviceList = deviceList;
    }
}
