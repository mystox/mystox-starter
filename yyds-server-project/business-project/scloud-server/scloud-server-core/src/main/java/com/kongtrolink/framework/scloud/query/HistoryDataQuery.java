package com.kongtrolink.framework.scloud.query;

/**
 * 历史数据界面查询参数实体类
 * @author Mag
 **/
public class HistoryDataQuery  extends Paging{

    private long startTime; //开始时间
    private long endTime;   //结束时间
    private String fsuCode;
    private String deviceCode;
    private String cntbId;//需要查询的具体信号点ID

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getFsuCode() {
        return fsuCode;
    }

    public void setFsuCode(String fsuCode) {
        this.fsuCode = fsuCode;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }
}
