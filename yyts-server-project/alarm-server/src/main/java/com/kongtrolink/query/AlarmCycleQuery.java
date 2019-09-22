package com.kongtrolink.query;

import com.kongtrolink.base.FacadeView;
import com.kongtrolink.base.Paging;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/21 10:59
 * @Description:
 */
public class AlarmCycleQuery extends Paging{

    private String id;
    private String uniqueCode;
    private String service;
    private String propertyStr;
    private Integer diffTime;
    private FacadeView creator;
    private Date beginTime;
    private Date endTime;

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPropertyStr() {
        return propertyStr;
    }

    public void setPropertyStr(String propertyStr) {
        this.propertyStr = propertyStr;
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
}
