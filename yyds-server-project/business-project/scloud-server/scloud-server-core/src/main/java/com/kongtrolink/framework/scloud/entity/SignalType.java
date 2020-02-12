package com.kongtrolink.framework.scloud.entity;

public class SignalType {

    private String code;
    private String typeName;  //信号点名称
    private String measurement; //单位
    private String cntbId;  //铁塔的ID
    private String type;    //信号点类型。"0"-遥信信号(DI),"1"-遥测信号(AI),"2"-遥控信号(DO),"3"-遥调信号(AO)
    private String alarmLevel;//告警级别
    private boolean communicationError;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getCntbId() {
        return cntbId;
    }

    public void setCntbId(String cntbId) {
        this.cntbId = cntbId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCommunicationError() {
        return communicationError;
    }

    public void setCommunicationError(boolean communicationError) {
        this.communicationError = communicationError;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    @Override
    public String toString() {
        return "SignalType{" + "code=" + code + ", typeName=" + typeName + ", measurement=" + measurement + ", cntbId=" + cntbId + ", type" + type + ", communicationError=" + communicationError + '}';
    }
}
