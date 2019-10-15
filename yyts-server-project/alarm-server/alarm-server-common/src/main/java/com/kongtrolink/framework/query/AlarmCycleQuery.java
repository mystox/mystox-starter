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
    private String enterpriseCode;
    private String serverCode;
    private Integer diffTime;
    private FacadeView creator;
    private Date beginTime;
    private Date endTime;
    private String state;

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

    public FacadeView getCreator() {
        return creator;
    }

    public void setCreator(FacadeView creator) {
        this.creator = creator;
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
