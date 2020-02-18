package com.kongtrolink.framework.scloud.entity.model;

import java.util.List;

/**
 * 企业前端显示模型
 *  使用场景：【系统管理-企业管理】
 * Created by Eric on 2020/2/10.
 */
public class CompanyModel {
    //企业信息（业务平台只保存扩展信息）
    private String uniqueCode;  //企业唯一码
    private String companyName; //企业名称
    private String serverName;  //服务名称
    private String agent;   //代理
    private String contactsId;  //企业联系人Id
    private String contactsName;    //企业联系人名称
    private String contactsPhone;   //企业联系人电话

    //企业业务配置
    private int pollingInterval;    //FSU数据轮询间隔（min）
    private int refreshInterval; //页面刷新间隔（s）
    private int fsuOfflineDelayTime;    //FSU离线告警延时时间

    //告警提醒配置
    private boolean alarmReminderOpen;    //是否开启告警提醒（弹窗）
    private List<String> alarmLevels;   //播报告警级别
    private List<String> alarmFields;  //播报内容，可对企业服务下的区域、站点、机房、设备、信号、告警编码ID、告警级别、告警时间进行设置播报内容字段
    private int cycleTimes; //循环次数
    private int flashFrequency;    //灯闪频率，次/分钟

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getContactsId() {
        return contactsId;
    }

    public void setContactsId(String contactsId) {
        this.contactsId = contactsId;
    }

    public String getContactsName() {
        return contactsName;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName;
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public int getFsuOfflineDelayTime() {
        return fsuOfflineDelayTime;
    }

    public void setFsuOfflineDelayTime(int fsuOfflineDelayTime) {
        this.fsuOfflineDelayTime = fsuOfflineDelayTime;
    }

    public boolean isAlarmReminderOpen() {
        return alarmReminderOpen;
    }

    public void setAlarmReminderOpen(boolean alarmReminderOpen) {
        this.alarmReminderOpen = alarmReminderOpen;
    }

    public List<String> getAlarmLevels() {
        return alarmLevels;
    }

    public void setAlarmLevels(List<String> alarmLevels) {
        this.alarmLevels = alarmLevels;
    }

    public List<String> getAlarmFields() {
        return alarmFields;
    }

    public void setAlarmFields(List<String> alarmFields) {
        this.alarmFields = alarmFields;
    }

    public int getCycleTimes() {
        return cycleTimes;
    }

    public void setCycleTimes(int cycleTimes) {
        this.cycleTimes = cycleTimes;
    }

    public int getFlashFrequency() {
        return flashFrequency;
    }

    public void setFlashFrequency(int flashFrequency) {
        this.flashFrequency = flashFrequency;
    }
}
