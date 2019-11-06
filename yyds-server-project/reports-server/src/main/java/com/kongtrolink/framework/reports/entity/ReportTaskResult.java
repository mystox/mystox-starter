package com.kongtrolink.framework.reports.entity;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/10/24, 19:17.
 * company: kongtrolink
 * description:
 * update record:
 */
public class ReportTaskResult {
    private String id;
    private String taskId;
    private String runId;
    private Date startTime;
    private Date recordTime;
    private ReportData result;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public ReportData getResult() {
        return result;
    }

    public void setResult(ReportData result) {
        this.result = result;
    }
}
