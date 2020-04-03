package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.WorkConstants;

import java.util.Date;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 13:54
 * @Description:山东国动告警实体类
 */
public class Alarm {

    private String id;
    private String enterpriseCode;
    private String serverCode;
    private String serial;              //告警序列号
    private String name;                //告警名称
    private float value;                //告警值
    private Integer level;               //告警等级
    private String siteCode;
    private String siteName;
    private String siteType;	        //站点类型
    private String tierName;
    private String siteAddress;
    private String deviceType;          //设备类型，设备类型，与资管一致
    private String deviceModel;         //设备型号，设备型号，如果没有与deviceType一致
    private String deviceId;            //设备对应的编码，需要与资产管理对应，设备id，即SN
    private String deviceName;
    private String signalId;            //信号点id，信号点id必须有，如果消息报文不包含信号点，则需要根据业务定义相关信号点
    private String signalName;          //信号点名称（告警名称）
    private Integer targetLevel;         //目标等级
    private String targetLevelName;     //目标等级名称
    private String color;               //告警颜色
    private Date treport;               //上报时间
    private Date trecover;              //消除时间
    private String state;               //告警状态(待处理，已消除)
    private Map<String, String> AuxilaryMap;    //附加属性列map
    private Map<String, String> deviceInfos;    //设备信息map
    private String type;                //告警类型（实时/历史）
    private Date checkTime;             //确认时间
    private String checkContant;        //确认内容
    private String checkState = BaseConstant.NOCHECK;          //告警确认状态
    private FacadeView checker;         //确认人
    private Boolean shield;             //是否屏蔽
    private String key ;                //唯一键，可作为索引

    public String initKey(){
        this.key = enterpriseCode + WorkConstants.UNDERLINE + serverCode + WorkConstants.COLON + deviceId + WorkConstants.UNDERLINE + serial;
        return key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCheckState() {

        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public Boolean getShield() {
        return shield;
    }

    public void setShield(Boolean shield) {
        this.shield = shield;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }

    public String getTargetLevelName() {
        return targetLevelName;
    }

    public void setTargetLevelName(String targetLevelName) {
        this.targetLevelName = targetLevelName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<String, String> getAuxilaryMap() {
        return AuxilaryMap;
    }

    public void setAuxilaryMap(Map<String, String> auxilaryMap) {
        AuxilaryMap = auxilaryMap;
    }

    public Map<String, String> getDeviceInfos() {
        return deviceInfos;
    }

    public void setDeviceInfos(Map<String, String> deviceInfos) {
        this.deviceInfos = deviceInfos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckContant() {
        return checkContant;
    }

    public void setCheckContant(String checkContant) {
        this.checkContant = checkContant;
    }

    public FacadeView getChecker() {
        return checker;
    }

    public void setChecker(FacadeView checker) {
        this.checker = checker;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "enterpriseCode='" + enterpriseCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", level=" + level +
                ", deviceType='" + deviceType + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", signalId='" + signalId + '\'' +
                ", targetLevel=" + targetLevel +
                ", targetLevelName='" + targetLevelName + '\'' +
                ", color='" + color + '\'' +
                ", treport=" + treport +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
