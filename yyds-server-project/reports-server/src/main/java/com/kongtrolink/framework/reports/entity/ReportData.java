package com.kongtrolink.framework.reports.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/10/29, 16:01.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ReportData {
    private String dataType;
    private String dataResult;

    @JSONField(serialize = false)
    private Date nextStartTime;
    public ReportData() {
    }

    public ReportData(String dataType, String dataResult) {
        this.dataType = dataType;
        this.dataResult = dataResult;
    }

    public Date getNextStartTime() {
        return nextStartTime;
    }

    public void setNextStartTime(Date nextStartTime) {
        this.nextStartTime = nextStartTime;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataResult() {
        return dataResult;
    }

    public void setDataResult(String dataResult) {
        this.dataResult = dataResult;
    }
}
