package com.kongtrolink.framework.query;

import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.base.Paging;
import com.kongtrolink.framework.enttiy.AlarmCycle;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 10:59
 * @Description:
 */
public class AlarmCycleQuery extends Paging{

    private String id;
    private String name;
    private String enterpriseCode;
    private String enterpriseName;
    private String serverCode;
    private String serverName;
    private Integer diffTime;
    private Date beginTime;
    private Date endTime;
    private String state;
    private String operatorName;
    private FacadeView operator;

    public FacadeView getOperator() {
        return operator;
    }

    public void setOperator(FacadeView operator) {
        this.operator = operator;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getDiffTime() {
        return diffTime;
    }

    public void setDiffTime(Integer diffTime) {
        this.diffTime = diffTime;
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

    public static AlarmCycleQuery alarmCycle2Query(AlarmCycle alarmCycle){
        if(null == alarmCycle){
            return null;
        }
        AlarmCycleQuery alarmCycleQuery = new AlarmCycleQuery();
        alarmCycleQuery.setEnterpriseCode(alarmCycle.getEnterpriseCode());
        alarmCycle.setServerCode(alarmCycle.getServerCode());
        alarmCycle.setState(alarmCycle.getState());
        return alarmCycleQuery;
    }
}
