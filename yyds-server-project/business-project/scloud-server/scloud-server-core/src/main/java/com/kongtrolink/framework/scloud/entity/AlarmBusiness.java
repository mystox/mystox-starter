package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.WorkConstants;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 18:21
 * @Description: 业务模块告警信息
 */
public class AlarmBusiness {

    private String id;      //与告警id无关
    private float value;                //告警值
    private String tierCode;
    private String tierName;
    private String siteCode;
    private String siteName;
    private String siteType;	        //站点类型
    private String siteAddress;
    private String deviceType;
    private String deviceModel;
    private String deviceCode;
    private String deviceName;
    private String cntbId;
    private String signalName;
    private String name;
    private Integer level;
    private String state;
    private String checkState = BaseConstant.NOCHECK;          //告警确认状态
    private Date checkTime;             //确认时间
    private String checkContant;        //确认内容
    private FacadeView checker;         //确认人
    private Date treport;
    private Date trecover;
    private String key;     //对应告警表中key，可唯一性从告警表获取数据
    private String entDevSig;           //enterprise_CodedeviceId_signalId,用于告警关注，屏蔽等功能
    private String workCode;
    private boolean shield = false;             //是否屏蔽
    private String shieldRuleId;        //屏蔽规则id
    int flag;
    private String focusId;             //关注点id，用于前端取消关注

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public String getTierCode() {
        return tierCode;
    }

    public void setTierCode(String tierCode) {
        this.tierCode = tierCode;
    }

    public boolean isShield() {
        return shield;
    }

    public void setShield(boolean shield) {
        this.shield = shield;
    }

    public String getFocusId() {
        return focusId;
    }

    public void setFocusId(String focusId) {
        this.focusId = focusId;
    }

    public String getShieldRuleId() {
        return shieldRuleId;
    }

    public void setShieldRuleId(String shieldRuleId) {
        this.shieldRuleId = shieldRuleId;
    }

    public String getEntDevSig() {
        return entDevSig;
    }

    public void setEntDevSig(String entDevSig) {
        this.entDevSig = entDevSig;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
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

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public static AlarmBusiness createByAlarm(Alarm alarm){
        AlarmBusiness business = new AlarmBusiness();
        business.setValue(alarm.getValue());
        business.setSiteCode(alarm.getSiteCode());
        business.setSiteName(alarm.getSiteName());
        business.setSiteType(alarm.getSiteType());
        business.setSiteAddress(alarm.getSiteAddress());
        business.setDeviceType(alarm.getDeviceType());
        business.setDeviceModel(alarm.getDeviceModel());
        business.setDeviceCode(alarm.getDeviceId());
        business.setDeviceName(alarm.getDeviceName());
        business.setCntbId(alarm.getSignalId());
        business.setName(alarm.getName());
        business.setLevel(alarm.getLevel());
        business.setState("待处理");
        business.setTreport(alarm.getTreport());
        business.setTrecover(alarm.getTrecover());
        business.setKey(alarm.getKey());
        business.setEntDevSig(alarm.getEnterpriseCode() + WorkConstants.UNDERLINE + alarm.getDeviceId() + WorkConstants.UNDERLINE + alarm.getSignalId());
        return business;
    }
}
