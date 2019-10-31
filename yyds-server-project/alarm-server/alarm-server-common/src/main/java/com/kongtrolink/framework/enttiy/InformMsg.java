package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.StringUtil;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/10/22 10:06
 * @Description: 通知内容
 */
public class InformMsg {

    private String _id;
    private String enterpriseCode;
    private String enterpriseName;
    private String serverCode;
    private String serverName;

    private String url;
    private String alarmName;
    private String deviceId;
    private String address;
    private String addressName;
    private String userId;
    private String signalId;            //信号点id
    private String serial;              //告警id

    private String type;                //类型（短信，邮件，APP）
    private String serverVerson;      //调用投递模块服务编码
    private String operateCode;          //调用投递模块的操作码
    private String tempCode;            //模板编码（短信通知中产生）
    private String informAccount;       //通知账号（电话号码/邮件/APP账号）

    private Date date;
    private int count;                  //重复次数
    private int currentTime;            //当前次数
    private String alarmStateType;      //告警类型（告警产生，告警消除，FSU离线告警）
    private String result;             //发送结果

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InformMsg informMsg = (InformMsg) o;

        if (!enterpriseCode.equals(informMsg.enterpriseCode)) return false;
        if (!serverCode.equals(informMsg.serverCode)) return false;
        if (!deviceId.equals(informMsg.deviceId)) return false;
        if (!userId.equals(informMsg.userId)) return false;
        if (!signalId.equals(informMsg.signalId)) return false;
        if (!serial.equals(informMsg.serial)) return false;
        return type.equals(informMsg.type);
    }

    @Override
    public int hashCode() {
        int result = enterpriseCode.hashCode();
        result = 31 * result + serverCode.hashCode();
        result = 31 * result + deviceId.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + signalId.hashCode();
        result = 31 * result + serial.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "InformMsg{" +
                "enterpriseCode='" + enterpriseCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", url='" + url + '\'' +
                ", alarmName='" + alarmName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", address='" + address + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", serverVerson='" + serverVerson + '\'' +
                ", operateCode='" + operateCode + '\'' +
                ", tempCode='" + tempCode + '\'' +
                ", informAccount='" + informAccount + '\'' +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getInformAccount() {
        return informAccount;
    }

    public void setInformAccount(String informAccount) {
        this.informAccount = informAccount;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServerVerson() {
        return serverVerson;
    }

    public void setServerVerson(String serverVerson) {
        this.serverVerson = serverVerson;
    }

    public String getOperateCode() {
        return operateCode;
    }

    public void setOperateCode(String operateCode) {
        this.operateCode = operateCode;
    }

    public String getTempCode() {
        return tempCode;
    }

    public void setTempCode(String tempCode) {
        this.tempCode = tempCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public String getAlarmStateType() {
        return alarmStateType;
    }

    public void setAlarmStateType(String alarmStateType) {
        this.alarmStateType = alarmStateType;
    }

    public InformMsg initAlarmInfo(Alarm alarm, InformRule informRule, InformRuleUser ruleUser, String type, Date date){
        this.setEnterpriseCode(informRule.getEnterpriseCode());
        this.enterpriseName = informRule.getEnterpriseName();
        this.setServerCode(informRule.getServerCode());
        this.serverName = informRule.getServerName();
        this.setType(type);
        this.userId = ruleUser.getUser().getStrId();
        this.setAlarmName(alarm.getName());
        this.setDeviceId(alarm.getDeviceId());
        this.setCount(informRule.getRepeat());
        this.date = date;
        this.signalId = alarm.getSignalId();
        this.serial = alarm.getSerial();
        String flag = alarm.getFlag();
        initFlagInfo(flag, informRule);
        return this;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/31 9:36
     * 功能描述:保存根据flag相关信息
     */
    private void initFlagInfo(String flag, InformRule informRule){
        //保存模板编码和模板
        if(Contant.ONE.equals(flag)){
            this.alarmStateType = Contant.ALARM_STATE_REPORT;
        }else if(Contant.ZERO.equals(flag)){
            this.alarmStateType = Contant.ALARM_STATE_RECOVER;
        }
        if(Contant.INFORM_TYPE_MSG.equals(type)){
            this.url = informRule.getMsgServerURL();
            this.serverVerson = informRule.getMsgServerVerson();
            this.operateCode = informRule.getMsgOperaCode();
            if(Contant.ONE.equals(flag)){
                this.tempCode = informRule.getMsgReportCode();
            }else if(Contant.ZERO.equals(flag)){
                this.tempCode = informRule.getMsgResolveCode();
            }
        }else if(Contant.INFORM_TYPE_EMAL.equals(type)){
            this.url = informRule.getEmailServerURL();
            this.serverVerson = informRule.getEmailServerVerson();
            this.operateCode = informRule.getEmailOperaCode();
            if(Contant.ONE.equals(flag)){
                this.tempCode = informRule.getEmailReportCode();
            }else{
                this.tempCode = informRule.getEmailResolveCode();
            }
        }else if(Contant.INFORM_TYPE_APP.equals(type)){
            this.url = informRule.getAppServerURL();
            this.serverVerson = informRule.getAppServerVerson();
            this.operateCode = informRule.getAppOperaCode();
            if(Contant.ONE.equals(flag)){
                this.tempCode = informRule.getAppReportCode();
            }else{
                this.tempCode = informRule.getAppResolveCode();
            }
        }
    }
}
