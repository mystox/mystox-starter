package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.constant.WorkConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 11:09
 * @Description:
 */
public class Work {

    private String id;                  //工单id
    private String code;                //流水号
    private String sendType;            //派单类型
    private String state;              //工单状态
    private FacadeView worker;          //当前处理人员
    private FacadeView site;            //站点(站点编码和站点名称)
    private String siteType;	//站点类型
    private FacadeView device;          //所在设备
    private String deviceType;          //设备类型
    private int taskTime;               //任务时长（小时）
    private String companyName;         //代维公司
    private Date sentTime;              //派单时间
    private Date receTime;              //接单时间
    private int csjdsc;                 //接单超时时长(minu)
    private Date operatorTime;          //最后操作时间，用于计算工单工单流转中的处理时长
    private Date backTime;              //回单时间
    private int cshdsc;                 //回单超时时长
    private Date csjdsj;                              //超时接单时间
    private int csjdtxjg;                             //超时接单提醒间隔
    private Date cshdsj;                              //超时回单时间
    private int cshdtxjg;                             //超时回单提醒周期
    private String isReceOverTime = WorkConstants.JUDGE_NO;     //是否超时接单
    private String isBackOverTime = WorkConstants.JUDGE_NO;     //是否超时回单
    private List<WorkAlarm> workAlarmList = new ArrayList<>();
    private String alarmState = WorkConstants.ALARM_STATE_PENDING; //告警状态
    private int pendingCount;                    //待处理告警数量
    //前端返回需要
    List<WorkRecord> workRecordList;


    //导出需要内容
    private Date alarmTime; //告警时间
    private String alarmStatus;
    private Integer alarmLevel;
    private String targetLevelName;
    private String alarmName;

    public String getTargetLevelName() {
        return targetLevelName;
    }

    public void setTargetLevelName(String targetLevelName) {
        this.targetLevelName = targetLevelName;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(String alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public List<WorkRecord> getWorkRecordList() {
        return workRecordList;
    }

    public void setWorkRecordList(List<WorkRecord> workRecordList) {
        this.workRecordList = workRecordList;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    public int getCsjdsc() {
        return csjdsc;
    }

    public void setCsjdsc(int csjdsc) {
        this.csjdsc = csjdsc;
    }

    public int getCshdsc() {
        return cshdsc;
    }

    public void setCshdsc(int cshdsc) {
        this.cshdsc = cshdsc;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public List<WorkAlarm> getWorkAlarmList() {
        return workAlarmList;
    }

    public void setWorkAlarmList(List<WorkAlarm> workAlarmList) {
        this.workAlarmList = workAlarmList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public FacadeView getWorker() {
        return worker;
    }

    public void setWorker(FacadeView worker) {
        this.worker = worker;
    }

    public FacadeView getSite() {
        return site;
    }

    public void setSite(FacadeView site) {
        this.site = site;
    }

    public FacadeView getDevice() {
        return device;
    }

    public void setDevice(FacadeView device) {
        this.device = device;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(int taskTime) {
        this.taskTime = taskTime;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public Date getReceTime() {
        return receTime;
    }

    public void setReceTime(Date receTime) {
        this.receTime = receTime;
    }

    public Date getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Date operatorTime) {
        this.operatorTime = operatorTime;
    }

    public Date getBackTime() {
        return backTime;
    }

    public void setBackTime(Date backTime) {
        this.backTime = backTime;
    }

    public Date getCsjdsj() {
        return csjdsj;
    }

    public void setCsjdsj(Date csjdsj) {
        this.csjdsj = csjdsj;
    }

    public int getCsjdtxjg() {
        return csjdtxjg;
    }

    public void setCsjdtxjg(int csjdtxjg) {
        this.csjdtxjg = csjdtxjg;
    }

    public Date getCshdsj() {
        return cshdsj;
    }

    public void setCshdsj(Date cshdsj) {
        this.cshdsj = cshdsj;
    }

    public int getCshdtxjg() {
        return cshdtxjg;
    }

    public void setCshdtxjg(int cshdtxjg) {
        this.cshdtxjg = cshdtxjg;
    }

    public String getIsReceOverTime() {
        return isReceOverTime;
    }

    public void setIsReceOverTime(String isReceOverTime) {
        this.isReceOverTime = isReceOverTime;
    }

    public String getIsBackOverTime() {
        return isBackOverTime;
    }

    public void setIsBackOverTime(String isBackOverTime) {
        this.isBackOverTime = isBackOverTime;
    }

    public String getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(String alarmState) {
        this.alarmState = alarmState;
    }

    public void initCSJDXX(Date curDate, WorkConfig workConfig){
        //设置超时接单相关信息
        long receWorkOverTime = curDate.getTime() + workConfig.getCsjdsc() * 60 * 1000;
        this.setCsjdsj(new Date(receWorkOverTime));
        this.setCsjdtxjg(workConfig.getCsjdtxjg());
    }

    public void initCSHDXX(Date curDate, WorkConfig workConfig){
        //设置超时回单相关信息
        long backWorkOverTime = curDate.getTime() + workConfig.getCshdsc() * 60 * 1000;
        this.setCshdsj(new Date(backWorkOverTime));
        this.setCshdtxjg(workConfig.getCshdtxjg());
    }

    public void countCSJDSJ(Date curDate){
        long curTime = curDate.getTime();
        long receOverTime = curTime - this.getCsjdsj().getTime();
        if(receOverTime > 0){
            this.setIsReceOverTime(WorkConstants.JUDGE_YES);
            int time = (int)(receOverTime / (1000 * 60 * 60));
            this.setCsjdsc(time);
        }
    }

    public void countCSHDSJ(Date curDate) {
        long curTime = curDate.getTime();
        long backOvetTime = curTime - this.cshdsj.getTime();
        if(backOvetTime > 0){
            this.setIsBackOverTime(WorkConstants.JUDGE_YES);
            int time = (int)(backOvetTime / (1000 * 60 * 60));
            this.setCshdsc(time);
        }
        int taskTime = (int)(curTime - this.getSentTime().getTime())/(1000*60*60);
        this.setTaskTime(taskTime);
    }

    public void increateAlarm(WorkAlarm workAlarm){
        this.workAlarmList.add(workAlarm);
        String state = workAlarm.getState();
        if(WorkConstants.ALARM_STATE_PENDING.equals(state)) {
            this.pendingCount++;
            this.alarmState = WorkConstants.ALARM_STATE_PENDING;
        }
    }

    public void decreateAlarm(){
        this.pendingCount --;
        if(0 == pendingCount){
            this.alarmState = WorkConstants.ALARM_STATE_RESOLVED;
        }
    }
}
