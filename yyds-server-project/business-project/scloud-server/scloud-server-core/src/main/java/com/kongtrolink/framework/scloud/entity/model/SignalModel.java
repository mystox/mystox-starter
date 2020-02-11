package com.kongtrolink.framework.scloud.entity.model;

import com.kongtrolink.framework.scloud.entity.realtime.SignalInfoEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 前端展现信号点信息
 */
public class SignalModel implements Serializable {
    private static final long serialVersionUID = 1744694293282116235L;
    private long reportTime;//采集时间
    private List<SignalInfoEntity> aiList;
    private List<SignalInfoEntity> aoList;
    private List<SignalInfoEntity> diList;
    private List<SignalInfoEntity> doList;

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public List<SignalInfoEntity> getAiList() {
        return aiList;
    }

    public void setAiList(List<SignalInfoEntity> aiList) {
        this.aiList = aiList;
    }

    public List<SignalInfoEntity> getAoList() {
        return aoList;
    }

    public void setAoList(List<SignalInfoEntity> aoList) {
        this.aoList = aoList;
    }

    public List<SignalInfoEntity> getDiList() {
        return diList;
    }

    public void setDiList(List<SignalInfoEntity> diList) {
        this.diList = diList;
    }

    public List<SignalInfoEntity> getDoList() {
        return doList;
    }

    public void setDoList(List<SignalInfoEntity> doList) {
        this.doList = doList;
    }
}
