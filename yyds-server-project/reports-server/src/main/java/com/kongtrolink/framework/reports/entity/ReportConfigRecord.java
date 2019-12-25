package com.kongtrolink.framework.reports.entity;

import java.util.Date;

/**
 * \* @Author: mystox
 * \* Date: 2019/12/19 16:32
 * \* Description:
 * \
 */

public class ReportConfigRecord {
    private String id;
    private String reportsTaskId;
    private String funcPrivCode;
    private String configUsername;
    private Date recordTime;

    public ReportConfigRecord() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportsTaskId() {
        return reportsTaskId;
    }

    public void setReportsTaskId(String reportsTaskId) {
        this.reportsTaskId = reportsTaskId;
    }

    public String getFuncPrivCode() {
        return funcPrivCode;
    }

    public void setFuncPrivCode(String funcPrivCode) {
        this.funcPrivCode = funcPrivCode;
    }


    public String getConfigUsername() {
        return configUsername;
    }

    public void setConfigUsername(String configUsername) {
        this.configUsername = configUsername;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
}