package com.kongtrolink.framework.core.entity;

import java.io.Serializable;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/3 10:13
 * \* Description:
 * \
 */
public class Fsu implements Serializable{
    private String _id;
    private String sn;
    private String fsuId;
    private String address;
    private String name;
    private String code;
    private String serialNum;
    private String coordinate;
    private Integer stationType;
    private Long setUpTime;
    private String desc;
    private String tierName;
    private String imsi; //IMSI串号
    private String operators;//运营商
    private Integer upgradeStatus; //升级状态标记
    private String version;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public Integer getUpgradeStatus()
    {
        return upgradeStatus;
    }

    public void setUpgradeStatus(Integer upgradeStatus)
    {
        this.upgradeStatus = upgradeStatus;
    }

    public String get_id()
    {
        return _id;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public String getCoordinate()
    {
        return coordinate;
    }

    public void setCoordinate(String coordinate)
    {
        this.coordinate = coordinate;
    }

    public String getImsi()
    {
        return imsi;
    }

    public void setImsi(String imsi)
    {
        this.imsi = imsi;
    }

    public String getOperators()
    {
        return operators;
    }

    public void setOperators(String operators)
    {
        this.operators = operators;
    }

    public String getTierName()
    {
        return tierName;
    }

    public void setTierName(String tierName)
    {
        this.tierName = tierName;
    }

    public Integer getStationType() {
        return stationType;
    }

    public void setStationType(Integer stationType) {
        this.stationType = stationType;
    }

    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }


    public Long getSetUpTime() {
        return setUpTime;
    }

    public void setSetUpTime(Long setUpTime) {
        this.setUpTime = setUpTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}