package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.Contant;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/10/22 10:06
 * @Description: 通知内容
 */
public class InformMsg {

    private String _id;
    private String enterpriseCode;
    private String serverCode;
    private String type;
    private String url;
    private String username;
    private String levelName;
    private String alarmName;
    private float value;                //告警值
    private Date treport;
    private String deviceId;
    private String msg;
    private Date date;
    private int count;          //重复次数
    private int currentTime;    //当前次数

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

    public InformMsg initAlarmInfo(Alarm alarm, InformRule informRule, InformRuleUser ruleUser, String type, Date date){
        this.setEnterpriseCode(informRule.getEnterpriseCode());
        this.setServerCode(informRule.getServerCode());
        this.setType(type);
        String url = informRule.getMsgServerURL();
        if(Contant.INFORM_TYPE_EMAL.equals(type)){
            url = informRule.getEmailServerURL();
        }else if(Contant.INFORM_TYPE_APP.equals(type)){
            url = informRule.getAppServerURL();
        }
        this.setUrl(url);
        //liuddtodo 这里可能需要保存信息模板
        this.setUsername(ruleUser.getUser().getStrId());
        this.setLevelName(alarm.getTargetLevelName());
        this.setAlarmName(alarm.getTargetLevelName());
        this.setValue(alarm.getValue());
        this.setTreport(alarm.getTreport());
        this.setDeviceId(alarm.getDeviceId());
        this.setCount(informRule.getCount());
        return this;
    }

    private String createMsg(InformRule informRule, InformRuleUser ruleUser, Date date){

        return null;
    }

    private String createEmail(InformRule informRule, InformRuleUser ruleUser, Date date){

        return null;
    }

    private String createAPP(InformRule informRule, InformRuleUser ruleUser, Date date){

        return null;
    }
}
