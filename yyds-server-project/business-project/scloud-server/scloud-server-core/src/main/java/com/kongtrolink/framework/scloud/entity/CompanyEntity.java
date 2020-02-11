package com.kongtrolink.framework.scloud.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 企业 数据实体类
 * Created by Eric on 2020/2/5.
 */
public class CompanyEntity implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 9128821310937466848L;
    private String _id;

    //企业信息（业务平台只保存扩展信息）
    private String uniqueCode;  //企业唯一码
    private String contactsId;  //企业联系人Id

    //企业业务配置
    private int pollingInterval = 30;    //FSU数据轮询间隔（min）。默认30min
    private int refreshInterval = 300; //页面刷新间隔（s）。默认5分钟
    private int fsuOfflineDelayTime;    //FSU离线告警延时时间

    //告警提醒配置
    private boolean alarmReminderOpen;    //是否开启告警提醒（弹窗）。默认关闭
    private List<String> alarmLevels;   //播报告警级别，默认为全部级别
    private List<String> alarmFields;  //播报内容，可对企业服务下的区域、站点、机房、设备、信号、告警编码ID、告警级别、告警时间进行设置播报内容字段。默认全部
    private int cycleTimes = 1; //循环次数。默认1次
    private int flashFrequency = 60;    //灯闪频率，次/分钟。默认60次/分钟

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getContactsId() {
        return contactsId;
    }

    public void setContactsId(String contactsId) {
        this.contactsId = contactsId;
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
