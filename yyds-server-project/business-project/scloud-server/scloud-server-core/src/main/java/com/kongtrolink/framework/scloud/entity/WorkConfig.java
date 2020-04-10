package com.kongtrolink.framework.scloud.entity;

import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.util.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * @auther: liudd
 * @date: 2020/4/1 10:08
 * 功能描述:告警工单配置信息
 */
public class WorkConfig {

    private String id ;                         //id
    private String sendType = WorkConstants.SEND_TYPE_MANUAL;    //派单方式
    private List<String> siteCodeList;          //启用范围，站点编码列表
    private List<String> siteTypeList;          //局站类型集合
    private List<String> alarmLevelList;        //告警级别
    private List<String> alarmNameList;        //告警名称
    private int reportAfter;                    //告警发生后多少分钟派单（用于自动派单）
    private String reportBegin;                 //告警产生时间开始时间
    private String reportEnd;                   //告警产生时间结束时间
    private int reportBeginInt;                 //告警产生时间开始时间int型，用于判断比对
    private int reportEndInt;                   //告警产生时间结束时间int行，用于判断比对
    private int csjdsc;                         //超时接单时长
    private int csjdtxjg;                       //超时接单提醒间隔
    private int cshdsc;                         //超时回单时长
    private int cshdtxjg;                       //超时回单提醒周期
    private Date updateTime;                    //修改时间，为修改则为创建时间
    private String type = WorkConstants.WORK_CONFIG_TYPE_MANUAL;      //工单配置类型，默认是人为创造

    public int getCsjdsc() {
        return csjdsc;
    }

    public void setCsjdsc(int csjdsc) {
        this.csjdsc = csjdsc;
    }

    public int getCshdsc() {
        return cshdsc;
    }

    public void setCshdsc(int cshdsc) {
        this.cshdsc = cshdsc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public List<String> getSiteCodeList() {
        return siteCodeList;
    }

    public void setSiteCodeList(List<String> siteCodeList) {
        this.siteCodeList = siteCodeList;
    }

    public List<String> getSiteTypeList() {
        return siteTypeList;
    }

    public void setSiteTypeList(List<String> siteTypeList) {
        this.siteTypeList = siteTypeList;
    }

    public List<String> getAlarmLevelList() {
        return alarmLevelList;
    }

    public void setAlarmLevelList(List<String> alarmLevelList) {
        this.alarmLevelList = alarmLevelList;
    }

    public List<String> getAlarmNameList() {
        return alarmNameList;
    }

    public void setAlarmNameList(List<String> alarmNameList) {
        this.alarmNameList = alarmNameList;
    }

    public int getReportAfter() {
        return reportAfter;
    }

    public void setReportAfter(int reportAfter) {
        this.reportAfter = reportAfter;
    }

    public String getReportBegin() {
        return reportBegin;
    }

    public void setReportBegin(String reportBegin) {
        this.reportBegin = reportBegin;
    }

    public String getReportEnd() {
        return reportEnd;
    }

    public void setReportEnd(String reportEnd) {
        this.reportEnd = reportEnd;
    }

    public int getReportBeginInt() {
        return reportBeginInt;
    }

    public void setReportBeginInt(int reportBeginInt) {
        this.reportBeginInt = reportBeginInt;
    }

    public int getReportEndInt() {
        return reportEndInt;
    }

    public void setReportEndInt(int reportEndInt) {
        this.reportEndInt = reportEndInt;
    }

    public int getCsjdtxjg() {
        return csjdtxjg;
    }

    public void setCsjdtxjg(int csjdtxjg) {
        this.csjdtxjg = csjdtxjg;
    }

    public int getCshdtxjg() {
        return cshdtxjg;
    }

    public void setCshdtxjg(int cshdtxjg) {
        this.cshdtxjg = cshdtxjg;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * @auther: liudd
     * @date: 2020/4/1 10:18
     * 功能描述:初始化开始时间和结束时间int型
     */
    public void initIntTime(){
        this.reportBeginInt = StringUtil.HHMMSSToInt(this.reportBegin);
        this.reportEndInt = StringUtil.HHMMSSToInt(this.reportEnd);
    }
}
