package com.kongtrolink.framework.scloud.query;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/9 13:34
 * @Description:
 */
public class AlarmBusinessQuery extends Paging {

    private String enterpriseCode;
    private String serverCode;
    private String key;
    private List<String> keyList;
    private List<Date> treportList;     //告警发生时间列表，用于批量告警消除
    private String name;                        //告警名称
    private Integer level;                      //告警等级
    private List<Integer> levelList;            //等级列表
    private String state;                       //告警状态(待处理，已消除)
    private String checkState;                  //告警确认状态
    private List<String> siteCodeList;
    private List<String> tierCodeList;
    private String deviceType;                  //设备类型，与资管一致
    private List<String> deviceTypeList;        //设备类型列表，用于告警过滤功能
    private String deviceModel;                 //设备型号，如果没有与deviceType一致
    private List<String> deviceCodeList;        //设备编码列表，用户数据权限最终转换成设备id列表，传递到中台告警模块查询
    private Date startBeginTime;                //发生开始时间
    private Date startEndTime;                  //发生结束时间
    private Date clearBeginTime;                //清除开始时间
    private Date clearEndTime;                  //清除结束时间
    private String type;                        //告警类型（实时/历史）

    private String operate;             //操作，告警确认，取消确认，告警消除
    private String operateDesc;             //操作描述
    private Date operateTime;         //操作对应的时间，方便远程接口参数解析
    private String operateUserId;           //操作用户id
    private String operateUsername;         //操作用户名
    private int skipSize;
    private List<String> entDevSigList;
    private boolean shield=false;                 //是否屏蔽
    private String shieldRuleId;            //屏蔽规则id

    public List<String> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<String> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }

    public List<String> getTierCodeList() {
        return tierCodeList;
    }

    public void setTierCodeList(List<String> tierCodeList) {
        this.tierCodeList = tierCodeList;
    }

    public boolean isShield() {
        return shield;
    }

    public void setShield(boolean shield) {
        this.shield = shield;
    }

    public Boolean getShield() {
        return shield;
    }

    public void setShield(Boolean shield) {
        this.shield = shield;
    }

    public String getShieldRuleId() {
        return shieldRuleId;
    }

    public void setShieldRuleId(String shieldRuleId) {
        this.shieldRuleId = shieldRuleId;
    }

    public List<String> getEntDevSigList() {
        return entDevSigList;
    }

    public void setEntDevSigList(List<String> entDevSigList) {
        this.entDevSigList = entDevSigList;
    }

    public int getSkipSize() {
        return skipSize;
    }

    public void setSkipSize(int skipSize) {
        this.skipSize = skipSize;
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

    public List<Date> getTreportList() {
        return treportList;
    }

    public void setTreportList(List<Date> treportList) {
        this.treportList = treportList;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getKeyList() {
        return keyList;
    }

    public void setKeyList(List<String> keyList) {
        this.keyList = keyList;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getOperateDesc() {
        return operateDesc;
    }

    public void setOperateDesc(String operateDesc) {
        this.operateDesc = operateDesc;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(String operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUsername() {
        return operateUsername;
    }

    public void setOperateUsername(String operateUsername) {
        this.operateUsername = operateUsername;
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

    public List<Integer> getLevelList() {
        return levelList;
    }

    public void setLevelList(List<Integer> levelList) {
        this.levelList = levelList;
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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<String> getDeviceTypeList() {
        return deviceTypeList;
    }

    public void setDeviceTypeList(List<String> deviceTypeList) {
        this.deviceTypeList = deviceTypeList;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public List<String> getDeviceCodeList() {
        return deviceCodeList;
    }

    public void setDeviceCodeList(List<String> deviceCodeList) {
        this.deviceCodeList = deviceCodeList;
    }

    public Date getStartBeginTime() {
        return startBeginTime;
    }

    public void setStartBeginTime(Date startBeginTime) {
        this.startBeginTime = startBeginTime;
    }

    public Date getStartEndTime() {
        return startEndTime;
    }

    public void setStartEndTime(Date startEndTime) {
        this.startEndTime = startEndTime;
    }

    public Date getClearBeginTime() {
        return clearBeginTime;
    }

    public void setClearBeginTime(Date clearBeginTime) {
        this.clearBeginTime = clearBeginTime;
    }

    public Date getClearEndTime() {
        return clearEndTime;
    }

    public void setClearEndTime(Date clearEndTime) {
        this.clearEndTime = clearEndTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static AlarmBusinessQuery createByAlarmQuery(AlarmQuery alarmQuery){
        AlarmBusinessQuery businessQuery = new AlarmBusinessQuery();
        businessQuery.setName(alarmQuery.getName());
        businessQuery.setLevel(alarmQuery.getLevel());
        businessQuery.setLevelList(alarmQuery.getLevelList());
        businessQuery.setState(alarmQuery.getState());
        businessQuery.setCheckState(alarmQuery.getCheckState());
        businessQuery.setTierCodeList(alarmQuery.getTierCodeList());
        businessQuery.setSiteCodeList(alarmQuery.getSiteCodeList());
        businessQuery.setDeviceType(alarmQuery.getDeviceType());
        businessQuery.setDeviceTypeList(alarmQuery.getDeviceTypeList());
        businessQuery.setDeviceModel(alarmQuery.getDeviceModel());
        businessQuery.setDeviceCodeList(alarmQuery.getDeviceCodeList());
        businessQuery.setStartBeginTime(alarmQuery.getStartBeginTime());
        businessQuery.setStartEndTime(alarmQuery.getStartEndTime());
        businessQuery.setClearBeginTime(alarmQuery.getClearBeginTime());
        businessQuery.setClearEndTime(alarmQuery.getClearEndTime());
        businessQuery.setType(alarmQuery.getType());
        businessQuery.setCurrentPage(alarmQuery.getCurrentPage());
        businessQuery.setPageSize(alarmQuery.getPageSize());
        businessQuery.setSkipSize(alarmQuery.getPageSize() * 5);
        alarmQuery.setName(null);
        alarmQuery.setLevel(null);
        alarmQuery.setLevelList(null);
        alarmQuery.setState(null);
        alarmQuery.setCheckState(null);
        alarmQuery.setTierCodeList(null);
        alarmQuery.setSiteCodeList(null);
        alarmQuery.setDeviceType(null);
        alarmQuery.setDeviceTypeList(null);
        alarmQuery.setDeviceModel(null);
        alarmQuery.setDeviceCodeList(null);
        alarmQuery.setStartBeginTime(null);
        alarmQuery.setStartEndTime(null);
        alarmQuery.setClearBeginTime(null);
        alarmQuery.setClearEndTime(null);
        return businessQuery;
    }
}
