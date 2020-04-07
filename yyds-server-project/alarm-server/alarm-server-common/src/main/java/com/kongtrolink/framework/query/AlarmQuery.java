package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Paging;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:44
 * @Description:
 */
public class AlarmQuery extends Paging {

    private String id;
    private List<String> idList;
    private String enterpriseCode;              //企业编码
    private String serverCode;                  //服务编码
    private String name;                        //告警名称
    private String deviceType;                  //设备型号
    private List<String> deviceTypeList;        //设备类型列表，用于告警过滤功能
    private String deviceModel;                 //设备类型
    private List<String> deviceCodeList;        //设备编码列表，需要统一
    private List<String> deviceIds;
    private String state;                       //告警状态
    private Integer level;
    private Integer targetLevel;
    private String targetLevelName;             //目标等级名称
    private String type;                        //告警类型（实时告警/历史告警）
    private Date startBeginTime;                //发生开始时间
    private Date startEndTime;                  //发生结束时间
    private Date clearBeginTime;                //清除开始时间
    private Date clearEndTime;                  //清除结束时间
    private Integer realBeginNum;               //真实分页起始数据
    private Integer realLimit;                  //真实分页大小
    private String checkState;          //告警确认状态
    private Date treport;                       //上报时间
    private Date trecover;              //消除时间

    //远程调用需要参数
    private String operate;             //操作，告警确认，取消确认，告警消除
    private Date operateTime;         //操作对应的时间，方便远程接口参数解析
    private String operateUsername;         //操作用户名
    private String operateName;           //操作用户姓名
    private String operateDesc;             //操作描述
    private List<Date> treportList;     //告警发生时间列表，用于批量告警消除
    private List<String> entDevSigList;     //enterpriseCode_deviceId_signalId列表
    private List<String> keys;         //告警键

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public List<String> getDeviceTypeList() {
        return deviceTypeList;
    }

    public void setDeviceTypeList(List<String> deviceTypeList) {
        this.deviceTypeList = deviceTypeList;
    }

    public List<String> getEntDevSigList() {
        return entDevSigList;
    }

    public void setEntDevSigList(List<String> entDevSigList) {
        this.entDevSigList = entDevSigList;
    }

    public Date getTrecover() {
        return trecover;
    }

    public void setTrecover(Date trecover) {
        this.trecover = trecover;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public List<Date> getTreportList() {
        return treportList;
    }

    public void setTreportList(List<Date> treportList) {
        this.treportList = treportList;
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

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public String getOperateUsername() {
        return operateUsername;
    }

    public void setOperateUsername(String operateUsername) {
        this.operateUsername = operateUsername;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public List<String> getDeviceCodeList() {
        return deviceCodeList;
    }

    public void setDeviceCodeList(List<String> deviceCodeList) {
        this.deviceCodeList = deviceCodeList;
    }

    public Date getTreport() {
        return treport;
    }

    public void setTreport(Date treport) {
        this.treport = treport;
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

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }

    public Integer getRealBeginNum() {
        return realBeginNum;
    }

    public void setRealBeginNum(Integer realBeginNum) {
        this.realBeginNum = realBeginNum;
    }

    public Integer getRealLimit() {
        return realLimit;
    }

    public void setRealLimit(Integer realLimit) {
        this.realLimit = realLimit;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
}
