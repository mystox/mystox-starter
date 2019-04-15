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
    private String BID;
    private Integer heartCycle;
    private Integer businessRhythm;
    private Integer alarmRhythm;
    private Integer runStatusRhythm;
    private String vendor;
    private String coordinate;

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
