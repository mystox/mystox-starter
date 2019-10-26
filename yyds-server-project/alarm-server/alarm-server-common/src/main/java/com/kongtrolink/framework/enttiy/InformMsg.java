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
    private String type;                //类型（短信，邮件，APP）
    private String url;
    private String levelName;
    private String alarmName;
    private float value;                //告警值
    private Date treport;
    private String deviceId;
    private String deviceName;
    private String address;
    private String addressName;
    private String userId;
    private String username;
    private String signalId;            //信号点id
    private String serial;              //告警id

    private String tempCode;            //模板编码（短信通知中产生）
    private String template;            //消息模板
    private String uniqueCode;      //调用投递模块服务编码
    private String operateCode;          //调用投递模块的操作码

    private String msg;                 //投递内容，在最终的投递动作模块生成
    private Date date;
    private int count;                  //重复次数
    private int currentTime;            //当前次数
    private String alarmStateType;           //告警类型（告警产生，告警消除，FSU离线告警）
    private String informAccount;       //通知账号（电话号码/邮件/APP账号）
    private boolean result;             //发送结果

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getAlarmStateType() {
        return alarmStateType;
    }

    public void setAlarmStateType(String alarmStateType) {
        this.alarmStateType = alarmStateType;
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

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public Date getTreport() {
        return treport;
    }

    public void setTreport(Date treport) {
        this.treport = treport;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public InformMsg initAlarmInfo(Alarm alarm, InformRule informRule, InformRuleUser ruleUser, String type, Date date){
        this.setEnterpriseCode(informRule.getEnterpriseCode());
        this.enterpriseName = informRule.getEnterpriseName();
        this.setServerCode(informRule.getServerCode());
        this.serverName = informRule.getServerName();
        this.setType(type);
        //保存模板编码和模板
        String flag = alarm.getFlag();
        if(Contant.ONE.equals(flag)){
            this.alarmStateType = Contant.ALARM_STATE_REPORT;
        }else if(Contant.ZERO.equals(flag)){
            this.alarmStateType = Contant.ALARM_STATE_RECOVER;
        }else {
            this.alarmStateType = Contant.ALARM_STATE_FSUOFFLINE;
        }
        if(Contant.INFORM_TYPE_MSG.equals(type)){
            this.url = informRule.getMsgServerURL();
            this.uniqueCode = informRule.getMsgServerVerson();
            this.operateCode = informRule.getMsgOperaCode();
            this.informAccount = ruleUser.getPhone();
            if(Contant.ONE.equals(flag)){
                this.tempCode = informRule.getMsgReportCode();
                this.template = informRule.getMsgReportModel();
            }else if(Contant.ZERO.equals(flag)){
                this.alarmStateType = Contant.ALARM_STATE_RECOVER;
                this.tempCode = informRule.getMsgResolveCode();
                this.tempCode = informRule.getMsgResolveModel();
            }else {
                this.alarmStateType = Contant.ALARM_STATE_FSUOFFLINE;
                this.tempCode = informRule.getFsuOfflineCode();
                this.template = informRule.getFsuOfflineModel();
            }
        }else if(Contant.INFORM_TYPE_EMAL.equals(type)){
            this.url = informRule.getEmailServerURL();
            this.uniqueCode = informRule.getEmailServerVerson();
            this.operateCode = informRule.getEmailOperaCode();
            this.informAccount = ruleUser.getEmail();
            if(Contant.ONE.equals(flag)){
                this.template = informRule.getEmailReportModel();
            }else{
                this.template = informRule.getEmailResolveModel();
            }
        }else if(Contant.INFORM_TYPE_APP.equals(type)){
            this.url = informRule.getAppServerURL();
            this.uniqueCode = informRule.getAppServerVerson();
            this.operateCode = informRule.getAppOperaCode();
            this.informAccount = ruleUser.getAppId();
            if(Contant.ONE.equals(flag)){
                this.template = informRule.getAppReportModel();
            }else{
                this.template = informRule.getAppResolveModel();
            }
        }
        this.userId = ruleUser.getUser().getStrId();
        this.setUsername(ruleUser.getUser().getName());
        this.setLevelName(alarm.getTargetLevelName());
        this.setAlarmName(alarm.getName());
        this.setValue(alarm.getValue());
        this.setTreport(alarm.getTreport());
        this.setDeviceId(alarm.getDeviceId());
        this.setCount(informRule.getCount());
        this.date = date;
        this.signalId = alarm.getSignalId();
        this.serial = alarm.getSerial();
        return this;
    }
}
