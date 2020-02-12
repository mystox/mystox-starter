package com.kongtrolink.framework.scloud.entity.model;

import com.kongtrolink.framework.scloud.entity.realtime.SignalInfoEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 前端展现信号点信息
 */
public class SignalModel implements Serializable {
    private static final long serialVersionUID = 1744694293282116235L;
    private long reportTime;//采集时间
    private Map<String,List<SignalInfoEntity>> infoList;//key:信号点类型 value:信号点列表

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public Map<String, List<SignalInfoEntity>> getInfoList() {
        return infoList;
    }

    public void setInfoList(Map<String, List<SignalInfoEntity>> infoList) {
        this.infoList = infoList;
    }
}
