package com.kongtrolink.framework.scloud.query;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 15:19
 * @Description:
 */
public class AlarmQuery {

    private String id;
    private String enterpriseCode;
    private String serverCode;
    private String name;                //告警名称
    private Integer targetLevel;         //目标等级
    private String targetLevelName;         //目标等级名称
    private String state;               //告警状态(待处理，已消除)
    private Boolean isCheck;              //是否确认
    private String deviceType;          //设备型号，设备类型，与资管一致
    private String deviceModel;         //设备型号，设备型号，如果没有与deviceType一致
    private String treport;             //告警发生时间
    private Date startBeginTime;                //发生开始时间
    private Date startEndTime;                  //发生结束时间
    private List<String> deviceCodeList;  //设备编码列表，用户数据权限最终转换成设备id列表，传递到中台告警模块查询
    private List<String> tierCodeList;  //区域层级编码列表
    private List<Integer> siteIdList;    //站点编码列表
    private String operationState;      //FSU运行状态
    private String type;                //告警类型（实时/历史）
    private String focus;              //告警关注操作(关注/取消关注)
    private Date focusTime;           //关注时间
    private String operate;             //操作，告警确认，取消确认，告警消除
    private Date operateTime;         //操作对应的时间，方便远程接口参数解析
    private String operateUserId;           //操作用户id
    private String operateUsername;         //操作用户名
    private String operateDesc;             //操作描述

    public String getOperateDesc() {
        return operateDesc;
    }

    public void setOperateDesc(String operateDesc) {
        this.operateDesc = operateDesc;
    }

    public String getOperateUsername() {
        return operateUsername;
    }

    public void setOperateUsername(String operateUsername) {
        this.operateUsername = operateUsername;
    }

    public String getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(String operateUserId) {
        this.operateUserId = operateUserId;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getTreport() {
        return treport;
    }

    public void setTreport(String treport) {
        this.treport = treport;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public Date getFocusTime() {
        return focusTime;
    }

    public void setFocusTime(Date focusTime) {
        this.focusTime = focusTime;
    }

    public String getTargetLevelName() {
        return targetLevelName;
    }

    public void setTargetLevelName(String targetLevelName) {
        this.targetLevelName = targetLevelName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperationState() {
        return operationState;
    }

    public void setOperationState(String operationState) {
        this.operationState = operationState;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getCheck() {
        return isCheck;
    }

    public void setCheck(Boolean check) {
        isCheck = check;
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

    public List<String> getDeviceCodeList() {
        return deviceCodeList;
    }

    public void setDeviceCodeList(List<String> deviceCodeList) {
        this.deviceCodeList = deviceCodeList;
    }

    public List<String> getTierCodeList() {
        return tierCodeList;
    }

    public void setTierCodeList(List<String> tierCodeList) {
        this.tierCodeList = tierCodeList;
    }

    public List<Integer> getSiteIdList() {
        return siteIdList;
    }

    public void setSiteIdList(List<Integer> siteIdList) {
        this.siteIdList = siteIdList;
    }
}
