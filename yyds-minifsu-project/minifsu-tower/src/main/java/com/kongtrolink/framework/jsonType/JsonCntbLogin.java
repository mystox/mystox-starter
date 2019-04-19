package com.kongtrolink.framework.jsonType;

import java.util.List;

/**
 * @author fengw
 * 铁塔注册参数
 * 2019-4-15 17:05:07 新建文件
 * */
public class JsonCntbLogin {

    private String userName;
    private String paSCWord;
    private String fsuId;
    private String ip;
    private String macId;
    private String imsiId;
    private String networkType;
    private String carrier;
    private String nmVendor;
    private String nmType;
    private int regMode;
    private String fsuVendor;
    private String lockedNetworkType;
    private String fsuType;
    private String fsuClass;
    private String version;
    private int dictVersion;
    private List<String> deviceIdList;

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

    public String getLockedNetworkType() {
        return lockedNetworkType;
    }

    public void setLockedNetworkType(String lockedNetworkType) {
        this.lockedNetworkType = lockedNetworkType;
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

    public List<String> getDeviceIdList() {
        return deviceIdList;
    }

    public void setDeviceIdList(List<String> deviceIdList) {
        this.deviceIdList = deviceIdList;
    }
}
