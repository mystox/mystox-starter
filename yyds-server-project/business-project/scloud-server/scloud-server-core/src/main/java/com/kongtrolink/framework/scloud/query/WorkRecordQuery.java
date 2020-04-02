package com.kongtrolink.framework.scloud.query;

/**
 * 工单记录表查询条件,
 */
public class WorkRecordQuery extends Paging {

    private String workId;          //工单id

    private String alarmId;         //告警id

    private String operatorId;      //操作人员id

    private String operatorPhone;   //操作人员联系电话

    //可能需要根据时间方面条件查询
    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorPhone() {
        return operatorPhone;
    }

    public void setOperatorPhone(String operatorPhone) {
        this.operatorPhone = operatorPhone;
    }
}
