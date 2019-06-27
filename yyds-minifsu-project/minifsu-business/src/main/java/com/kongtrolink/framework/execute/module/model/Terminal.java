package com.kongtrolink.framework.execute.module.model;

/**
 * Created by mystoxlol on 2019/3/28, 9:38.
 * company: kongtrolink
 * description:
 * update record:
 */
public class Terminal
{
    private String id;
    private String SN;
    private String type;
    private String BID = "default";
    private Integer heartCycle = 10;
    private Integer businessRhythm = 30;
    private Integer alarmRhythm =1;
    private Integer runStatusRhythm = 100;
    private String vendor;
    private String model = "mu001"; //终端型号 默认 mmu001
    private String coordinate;
    private String fsuId; //fsuId
    private boolean bindMark = false;
    private boolean enableHeart = true; //默认上报心跳

    private String name;
    private String address;

    private String userId;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isEnableHeart() {
        return enableHeart;
    }

    public void setEnableHeart(boolean enableHeart) {
        this.enableHeart = enableHeart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isBindMark() {
        return bindMark;
    }


    public String getFsuId() {
        return fsuId;
    }

    public void setFsuId(String fsuId) {
        this.fsuId = fsuId;
    }

    public void setBindMark(boolean bindMark) {
        this.bindMark = bindMark;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public Integer getAlarmRhythm() {
        return alarmRhythm;
    }

    public void setAlarmRhythm(Integer alarmRhythm) {
        this.alarmRhythm = alarmRhythm;
    }

    public Integer getHeartCycle() {
        return heartCycle;
    }

    public void setHeartCycle(Integer heartCycle) {
        this.heartCycle = heartCycle;
    }

    public Integer getBusinessRhythm() {
        return businessRhythm;
    }

    public void setBusinessRhythm(Integer businessRhythm) {
        this.businessRhythm = businessRhythm;
    }


    public Integer getRunStatusRhythm() {
        return runStatusRhythm;
    }

    public void setRunStatusRhythm(Integer runStatusRhythm) {
        this.runStatusRhythm = runStatusRhythm;
    }

    public String getSN()
    {
        return SN;
    }

    public void setSN(String SN)
    {
        this.SN = SN;
    }

    public String getBID()
    {
        return BID;
    }

    public void setBID(String BID)
    {
        this.BID = BID;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
