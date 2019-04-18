package com.kongtrolink.framework.jsonType;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @author fengw
 * 铁塔注册参数
 * 2019-4-15 17:05:07 新建文件
 * */
public class JsonLogin {

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPaSCWord(String paSCWord) {
        this.paSCWord = paSCWord;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public void setImsiId(String imsiId) {
        this.imsiId = imsiId;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setNmVendor(String nmVendor) {
        this.nmVendor = nmVendor;
    }

    public void setNmType(String nmType) {
        this.nmType = nmType;
    }

    public void setFsuVendor(String fsuVendor) {
        this.fsuVendor = fsuVendor;
    }

    public void setLockedNetworkType(String lockedNetworkType) {
        this.lockedNetworkType = lockedNetworkType;
    }

    public void setFsuType(String fsuType) {
        this.fsuType = fsuType;
    }

    public void setFsuClass(String fsuClass) {
        this.fsuClass = fsuClass;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDeviceIdList(List<String> deviceIdList) {
        this.deviceIdList = deviceIdList;
    }

    public String getUserName() {
        return userName;
    }

    public String getPaSCWord() {
        return paSCWord;
    }

    public String getFsuId() {
        return fsuId;
    }

    public String getIp() {
        return ip;
    }

    public String getMacId() {
        return macId;
    }

    public String getImsiId() {
        return imsiId;
    }

    public String getNetworkType() {
        return networkType;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getNmVendor() {
        return nmVendor;
    }

    public String getNmType() {
        return nmType;
    }

    public int getRegMode() {
        return regMode;
    }

    public String getFsuVendor() {
        return fsuVendor;
    }

    public String getLockedNetworkType() {
        return lockedNetworkType;
    }

    public String getFsuType() {
        return fsuType;
    }

    public String getFsuClass() {
        return fsuClass;
    }

    public String getVersion() {
        return version;
    }

    public int getDictVersion() {
        return dictVersion;
    }

    public List<String> getDeviceIdList() {
        return deviceIdList;
    }

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
    @Value("${tower.login.regMode}")
    private int regMode;
    private String fsuVendor;
    private String lockedNetworkType;
    private String fsuType;
    private String fsuClass;
    private String version;
    @Value("${tower.login.dictVersion}")
    private int dictVersion;
    private List<String> deviceIdList;
}
