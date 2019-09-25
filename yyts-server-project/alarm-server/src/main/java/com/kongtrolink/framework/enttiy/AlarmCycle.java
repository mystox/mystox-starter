package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.FacadeView;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 10:56
 * @Description:
 */
public class AlarmCycle {

    private String id;
    private String uniqueCode;
    private String service;
    private Integer diffTime;       //时间，必须大于0， -1表示默认，告警消除则成为历史告警
    private Date updateTime;
    private FacadeView creator;
    private String state = Contant.FORBIT;

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

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
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

    public FacadeView getCreator() {
        return creator;
    }

    public void setCreator(FacadeView creator) {
        this.creator = creator;
    }

    /**
     * @auther: liudd
     * @date: 2019/9/23 14:42
     * 功能描述:判断告警是否已经成为历史告警
     * 当前只支持上报时间和消除时间两个字段
     */
    public boolean isHistory(Alarm alarm, Date curDate){
        if(null == alarm){
            return false;
        }
        if(diffTime < 0){
            if(null != alarm.gettRecover()){
                return true;
            }
            return false;
        }
        Date tReport = alarm.gettReport();
        long time = curDate.getTime() - tReport.getTime();
        if(time >= (diffTime * 60 * 1000)){
            return true;
        }
        return false;
    }
}
