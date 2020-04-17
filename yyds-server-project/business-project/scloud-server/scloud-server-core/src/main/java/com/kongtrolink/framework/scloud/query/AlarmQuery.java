package com.kongtrolink.framework.scloud.query;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 15:19
 * @Description:
 */
public class AlarmQuery extends Paging{

    private String id;
    private List<String> idList;
    private String enterpriseCode;
    private String serverCode;
    private String name;                //告警名称
    private Integer level;
    private List<Integer> levelList;
    private String state;               //告警状态(待处理，已消除)
    private String checkState;          //告警确认状态
    private String deviceType;          //设备类型，与资管一致
    private List<String> deviceTypeList;        //设备类型列表，用于告警过滤功能
    private String deviceModel;         //设备型号，如果没有与deviceType一致
    private Date treport;             //告警发生时间
    private Date trecover;              //消除时间
    private List<Date> treportList;     //告警发生时间列表，用于批量告警消除
    private Date startBeginTime;                //发生开始时间
    private Date startEndTime;                  //发生结束时间
    private Date clearBeginTime;                //清除开始时间
    private Date clearEndTime;                  //清除结束时间
    private List<String> deviceCodeList;  //设备编码列表，用户数据权限最终转换成设备id列表，传递到中台告警模块查询
    private List<String> tierCodeList;  //区域层级编码列表
    private List<String> siteCodeList;      //设备编码列表
    private String operationState;      //FSU运行状态
    private String type;                //告警类型（实时/历史）
//    private String focus;              //告警关注操作(关注/取消关注)
//    private Date focusTime;           //关注时间
    private String operate;             //操作，告警确认，取消确认，告警消除
    private String operateDesc;             //操作描述
    private Date operateTime;         //操作对应的时间，方便远程接口参数解析
    private String operatorId;           //操作用户id
    private String operatorName;         //操作用户名

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    private List<String> keyList;

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
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

    public List<String> getDeviceTypeList() {
        return deviceTypeList;
    }

    public void setDeviceTypeList(List<String> deviceTypeList) {
        this.deviceTypeList = deviceTypeList;
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

    public Date getTrecover() {
        return trecover;
    }

    public void setTrecover(Date trecover) {
        this.trecover = trecover;
    }

    public List<Date> getTreportList() {
        return treportList;
    }

    public void setTreportList(List<Date> treportList) {
        this.treportList = treportList;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public Date getTreport() {
        return treport;
    }

    public void setTreport(Date treport) {
        this.treport = treport;
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

    public List<String> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<String> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }
}
