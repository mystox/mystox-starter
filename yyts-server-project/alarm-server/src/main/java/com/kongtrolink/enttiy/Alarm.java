package com.kongtrolink.enttiy;

import java.util.Date;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:19
 * @Description:告警基本类型
 */
public class Alarm {

    private String id;
    private String uniqueCode;
    private String service;
    private String name;        //告警名称
    private float value;        //告警值
    private String level;       //告警等级
    private Date tReport;       //上报时间
    private Date tRecover;      //消除时间
    private String deviceId;    //设备对应的编码，需要与资产管理对应
    private String state;
    private String cycleId;     //告警周期id
    private Map<String, String> AuxilaryMap;

    public Map<String, String> getAuxilaryMap() {
        return AuxilaryMap;
    }

    public void setAuxilaryMap(Map<String, String> auxilaryMap) {
        AuxilaryMap = auxilaryMap;
    }

    public String getCycleId() {
        return cycleId;
    }

    public void setCycleId(String cycleId) {
        this.cycleId = cycleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Date gettReport() {
        return tReport;
    }

    public void settReport(Date tReport) {
        this.tReport = tReport;
    }

    public Date gettRecover() {
        return tRecover;
    }

    public void settRecover(Date tRecover) {
        this.tRecover = tRecover;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUniqueService(){
        return this.uniqueCode + this.service;
    }
}
