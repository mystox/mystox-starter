package com.kongtrolink.framework.reports.entity;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/10/23, 9:41.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ReportTask {

    private String id;

    private String taskType; //报表任务类型：计划任务 一次性任务

    private String reportName;

    private int taskStatus;

    private Date startTime;

    private Date endTime;

    private String operaCode;

    private String serverCode;

    private String enterpriseCode;

    private Long operaValidity = -1L; //报表有效期

    private String reportServerVersion;

    private Boolean asyn = false;


    public ReportTask() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getOperaCode() {
        return operaCode;
    }

    public void setOperaCode(String operaCode) {
        this.operaCode = operaCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public Long getOperaValidity() {
        return operaValidity;
    }

    public void setOperaValidity(Long operaValidity) {
        this.operaValidity = operaValidity;
    }


    public String getReportServerVersion() {
        return reportServerVersion;
    }

    public void setReportServerVersion(String reportServerVersion) {
        this.reportServerVersion = reportServerVersion;
    }

    public Boolean getAsyn() {
        return asyn;
    }

    public void setAsyn(Boolean asyn) {
        this.asyn = asyn;
    }
}
