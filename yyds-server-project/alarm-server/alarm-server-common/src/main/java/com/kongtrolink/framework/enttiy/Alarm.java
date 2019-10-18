package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.Contant;

import java.util.Date;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:19
 * @Description:告警基本类型
 */
public class Alarm {

    private String id;
    private String enterpriseCode;
    private String serverCode;
    private String serial;              //告警序列号
    private String name;                //告警名称
    private float value;                //告警值
    private Integer level;               //告警等级
    private Integer targetLevel;         //目标等级
    private String targetLevelName;     //目标等级名称
    private String color;               //告警颜色
    private Date treport;               //上报时间
    private Date trecover;              //消除时间
    private String deviceId;            //设备对应的编码，需要与资产管理对应
    private String deviceType;          //设备型号
    private String deviceModel;         //设备类型
    private String signalId;            //信号点id
    private String state;
    private String sliceKey;            //片键，也可以作为普通索引键
    private Map<String, String> AuxilaryMap;

    private Map<String, String> deviceInfos;

    public Map<String, String> getDeviceInfos() {
        return deviceInfos;
    }

    public void setDeviceInfos(Map<String, String> deviceInfos) {
        this.deviceInfos = deviceInfos;
    }

    public void initSliceKey(){
        sliceKey = this.enterpriseCode + this.serverCode + deviceType + deviceModel;
    }

    public String getSliceKey() {
        return sliceKey;
    }

    public void setSliceKey(String sliceKey) {
        this.sliceKey = sliceKey;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTargetLevelName() {
        return targetLevelName;
    }

    public void setTargetLevelName(String targetLevelName) {
        this.targetLevelName = targetLevelName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Map<String, String> getAuxilaryMap() {
        return AuxilaryMap;
    }

    public void setAuxilaryMap(Map<String, String> auxilaryMap) {
        AuxilaryMap = auxilaryMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getTreport() {
        return treport;
    }

    public void setTreport(Date treport) {
        this.treport = treport;
    }

    public Date getTrecover() {
        return trecover;
    }

    public void setTrecover(Date trecover) {
        this.trecover = trecover;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEnterpriseServer(){
        return this.enterpriseCode + Contant.UNDERLINE + this.serverCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }
}
