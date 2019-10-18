package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.base.StringUtil;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 09:44
 * @Description:告警通知规则
 */
public class InformRule {

    private String _id;
    private String name;                    //名称
    private List<String> content;           //规则内容
    private String describe;                //备注
    private String type;                    //通知规则类型（auto-默认规则，manual-手动）.默认规则不允许修改和删除
    private String enterpriseCode;
    private String enterpriseName;
    private String serverCode;
    private String serverName;

    private String msgEnable;               //告警短信是否启用
    private String msgBeginTime;            //告警短信开始时间
    private String msgEndTime;              //告警短信结束时间
    private int msgBeginTimeInt;            //告警短信开始时间数值
    private int msgEndTimeInt;              //告警短信结束时间数值
    private List<Integer> msgDayList ;      //告警短信星期数组
    private List<Integer> msgLevelList;     //告警等级列表
    private String msgServerURL;

    private String emailEnable;               //告警邮件是否启用
    private String emailBeginTime;            //告警邮件开始时间
    private String emailEndTime;              //告警邮件结束时间
    private int emailBeginTimeInt;            //告警邮件开始时间数值
    private int emailEndTimeInt;              //告警邮件结束时间数值
    private List<Integer> emailDayList ;      //告警邮件星期数组
    private List<Integer> emailLevelList;     //告警邮件等级列表
    private String emailServerURL;

    private String appEnable;               //告警邮件是否启用
    private String appBeginTime;            //告警邮件开始时间
    private String appEndTime;              //告警邮件结束时间
    private int appBeginTimeInt;            //告警邮件开始时间数值
    private int appEndTimeInt;              //告警邮件结束时间数值
    private List<Integer> appDayList ;      //告警邮件星期数组
    private List<Integer> appLevelList;      //告警邮件等级列表
    private String appServerURL;

    private Date updateTime;                //修改时间
    private String status;                  //启用状态（启用，禁用）
    private FacadeView operator;            //操作用户

    public FacadeView getOperator() {
        return operator;
    }

    public void setOperator(FacadeView operator) {
        this.operator = operator;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getMsgServerURL() {
        return msgServerURL;
    }

    public void setMsgServerURL(String msgServerURL) {
        this.msgServerURL = msgServerURL;
    }

    public String getEmailServerURL() {
        return emailServerURL;
    }

    public void setEmailServerURL(String emailServerURL) {
        this.emailServerURL = emailServerURL;
    }

    public String getAppServerURL() {
        return appServerURL;
    }

    public void setAppServerURL(String appServerURL) {
        this.appServerURL = appServerURL;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsgEnable() {
        return msgEnable;
    }

    public void setMsgEnable(String msgEnable) {
        this.msgEnable = msgEnable;
    }

    public String getMsgBeginTime() {
        return msgBeginTime;
    }

    public void setMsgBeginTime(String msgBeginTime) {
        this.msgBeginTime = msgBeginTime;
    }

    public String getMsgEndTime() {
        return msgEndTime;
    }

    public void setMsgEndTime(String msgEndTime) {
        this.msgEndTime = msgEndTime;
    }

    public int getMsgBeginTimeInt() {
        return msgBeginTimeInt;
    }

    public void setMsgBeginTimeInt(int msgBeginTimeInt) {
        this.msgBeginTimeInt = msgBeginTimeInt;
    }

    public int getMsgEndTimeInt() {
        return msgEndTimeInt;
    }

    public void setMsgEndTimeInt(int msgEndTimeInt) {
        this.msgEndTimeInt = msgEndTimeInt;
    }

    public List<Integer> getMsgDayList() {
        return msgDayList;
    }

    public void setMsgDayList(List<Integer> msgDayList) {
        this.msgDayList = msgDayList;
    }

    public String getEmailEnable() {
        return emailEnable;
    }

    public void setEmailEnable(String emailEnable) {
        this.emailEnable = emailEnable;
    }

    public String getEmailBeginTime() {
        return emailBeginTime;
    }

    public void setEmailBeginTime(String emailBeginTime) {
        this.emailBeginTime = emailBeginTime;
    }

    public String getEmailEndTime() {
        return emailEndTime;
    }

    public void setEmailEndTime(String emailEndTime) {
        this.emailEndTime = emailEndTime;
    }

    public int getEmailBeginTimeInt() {
        return emailBeginTimeInt;
    }

    public void setEmailBeginTimeInt(int emailBeginTimeInt) {
        this.emailBeginTimeInt = emailBeginTimeInt;
    }

    public int getEmailEndTimeInt() {
        return emailEndTimeInt;
    }

    public void setEmailEndTimeInt(int emailEndTimeInt) {
        this.emailEndTimeInt = emailEndTimeInt;
    }

    public List<Integer> getEmailDayList() {
        return emailDayList;
    }

    public void setEmailDayList(List<Integer> emailDayList) {
        this.emailDayList = emailDayList;
    }

    public String getAppEnable() {
        return appEnable;
    }

    public void setAppEnable(String appEnable) {
        this.appEnable = appEnable;
    }

    public String getAppBeginTime() {
        return appBeginTime;
    }

    public void setAppBeginTime(String appBeginTime) {
        this.appBeginTime = appBeginTime;
    }

    public String getAppEndTime() {
        return appEndTime;
    }

    public void setAppEndTime(String appEndTime) {
        this.appEndTime = appEndTime;
    }

    public int getAppBeginTimeInt() {
        return appBeginTimeInt;
    }

    public void setAppBeginTimeInt(int appBeginTimeInt) {
        this.appBeginTimeInt = appBeginTimeInt;
    }

    public int getAppEndTimeInt() {
        return appEndTimeInt;
    }

    public void setAppEndTimeInt(int appEndTimeInt) {
        this.appEndTimeInt = appEndTimeInt;
    }

    public List<Integer> getAppDayList() {
        return appDayList;
    }

    public void setAppDayList(List<Integer> appDayList) {
        this.appDayList = appDayList;
    }

    public List<Integer> getMsgLevelList() {
        return msgLevelList;
    }

    public void setMsgLevelList(List<Integer> msgLevelList) {
        this.msgLevelList = msgLevelList;
    }

    public List<Integer> getEmailLevelList() {
        return emailLevelList;
    }

    public void setEmailLevelList(List<Integer> emailLevelList) {
        this.emailLevelList = emailLevelList;
    }

    public List<Integer> getAppLevelList() {
        return appLevelList;
    }

    public void setAppLevelList(List<Integer> appLevelList) {
        this.appLevelList = appLevelList;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    /**
     * @auther: liudd
     * @date: 2018/7/3 18:42
     * 功能描述:初始化告警短信，邮件，APP推送中的开始时间和结束时间数值
     */
    public void initDateInt(){
        if(!StringUtil.isNUll(this.msgBeginTime)){
            this.setMsgBeginTimeInt(DateUtil.HHMMSSToInt(this.msgBeginTime));
        }
        if(!StringUtil.isNUll(this.msgEndTime)){
            this.setMsgEndTimeInt(DateUtil.HHMMSSToInt(this.msgEndTime));
        }
        if(!StringUtil.isNUll(this.emailBeginTime)){
            this.setEmailBeginTimeInt(DateUtil.HHMMSSToInt(this.emailBeginTime));
        }
        if(!StringUtil.isNUll(this.emailEndTime)){
            this.setEmailEndTimeInt(DateUtil.HHMMSSToInt(this.emailEndTime));
        }
        if(!StringUtil.isNUll(this.appBeginTime)){
            this.setAppBeginTimeInt(DateUtil.HHMMSSToInt(this.appBeginTime));
        }
        if(!StringUtil.isNUll(this.appEndTime)){
            this.setAppEndTimeInt(DateUtil.HHMMSSToInt(this.appEndTime));
        }
    }
}
