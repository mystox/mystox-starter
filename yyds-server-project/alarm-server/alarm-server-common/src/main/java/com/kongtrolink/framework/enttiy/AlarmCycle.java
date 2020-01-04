package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.FacadeView;
import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 10:56
 * @Description:
 */
public class AlarmCycle {

    private String id;
    private String name;
    private String enterpriseCode;
    private String serverCode;
    private Integer diffTime;       //时间，必须大于0， -1表示默认，告警消除则成为历史告警
    private Date updateTime;
    private FacadeView operator;
    private String state ;
    private String enterpriseServer;    //企业和服务合体
    private String cycleType;           //周期类型（系统/手动）

    public String getCycleType() {
        return cycleType;
    }

    public void setCycleType(String cycleType) {
        this.cycleType = cycleType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void initEnterpirseServer(){
        this.enterpriseServer = this.enterpriseCode + Contant.UNDERLINE + this.serverCode;
    }
    public String getEnterpriseServer() {
        return enterpriseServer;
    }

    public void setEnterpriseServer(String enterpriseServer) {
        this.enterpriseServer = enterpriseServer;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public Integer getDiffTime() {
        return diffTime;
    }

    public void setDiffTime(Integer diffTime) {
        this.diffTime = diffTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public FacadeView getOperator() {
        return operator;
    }

    public void setOperator(FacadeView operator) {
        this.operator = operator;
    }

    /**
     * @auther: liudd
     * @date: 2019/9/23 14:42
     * 功能描述:判断告警是否已经成为历史告警
     * 当前只支持上报时间和消除时间两个字段
     */
    public static boolean isHistory(AlarmCycle alarmCycle, Alarm alarm, Date curDate){
        if(null == alarm){
            return false;
        }
        if(alarmCycle.diffTime < 0){
            if(null != alarm.getTrecover()){
                return true;
            }
            return false;
        }
        Date tReport = alarm.getTreport();
        int pastTime = alarmCycle.diffTime * 60 * 60 * 1000;
//        double pastTime = (0.05 * 60 * 60 * 1000);//liuddtodo 暂时写死3分钟就算历史
        long time = curDate.getTime() - tReport.getTime();
        if(time >= pastTime){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "AlarmCycle{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", enterpriseCode='" + enterpriseCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", diffTime=" + diffTime +
                ", updateTime=" + updateTime +
                ", operator=" + operator +
                ", state='" + state + '\'' +
                ", enterpriseServer='" + enterpriseServer + '\'' +
                ", cycleType='" + cycleType + '\'' +
                '}';
    }
}