package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.Paging;
import com.kongtrolink.framework.enttiy.Alarm;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:44
 * @Description:
 */
public class AlarmQuery extends Paging {

    private String id;
    private String enterpriseCode;
    private String serverCode;
    private String serial;              //告警序列号
    private String name;        //告警名称
    private float value;        //告警值
    private Date tReport;       //上报时间
    private Date tRecover;      //消除时间
    private String deviceId;       //设备对应的编码，需要与资产管理对应
    private String deviceType;  //设备型号
    private String deviceModel; //设备类型
    private Date beginTime;
    private Date endTime;
    private List<String> levelList;
    private List<String> alarmIdList;
    private String state;
    Map<String, String> deviceInfo; //设备信息作为查询条件
    private String targetLevel;     //目标等级
    private String targetLevelName;     //目标等级名称
    private String color;               //告警颜色
    private String signalId;            //信号点id
    private String sliceKey;            //片键，也可以作为普通索引键

    public String getSliceKey() {
        return sliceKey;
    }

    public void setSliceKey(String sliceKey) {
        this.sliceKey = sliceKey;
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

    public String getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(String targetLevel) {
        this.targetLevel = targetLevel;
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

    public Map<String, String> getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(Map<String, String> deviceInfo) {
        this.deviceInfo = deviceInfo;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getLevelList() {
        return levelList;
    }

    public void setLevelList(List<String> levelList) {
        this.levelList = levelList;
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

    public List<String> getAlarmIdList() {
        return alarmIdList;
    }

    public void setAlarmIdList(List<String> alarmIdList) {
        this.alarmIdList = alarmIdList;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static AlarmQuery alarm2AlarmQuery(Alarm alarm){
        if(null == alarm){
            return null;
        }
        AlarmQuery alarmQuery = new AlarmQuery();
        alarmQuery.setEnterpriseCode(alarm.getEnterpriseCode());
        alarmQuery.setServerCode(alarm.getServerCode());
        alarmQuery.setDeviceId(alarm.getDeviceId());
        alarmQuery.setDeviceType(alarm.getDeviceType());
        alarmQuery.setDeviceModel(alarm.getDeviceModel());
        alarmQuery.setSignalId(alarm.getSignalId());
        alarmQuery.setState(alarm.getState());
        alarmQuery.setSliceKey(alarm.getSliceKey());
        return alarmQuery;
    }
}
