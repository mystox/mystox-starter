package com.kongtrolink.framework.base;

/**
 * @Auther: liudd
 * @Date: 2019/9/16 13:59
 * @Description:
 */
public class MongTable {

    public static final String ENTERPRISE_LEVEL = "enterprise_level";       //企业服务告警等级和颜色表
    public static final String DEVICETYPE_LEVEL = "devicetype_level";       //企业-服务下，设备型号告警等级表
    public static final String ALARM_LEVEL = "alarm_level";                 //企业等级和设备信号源等级对应表
    public static final String ALARM_CURRENT = "alarm_current";
    public static final String ALARM_HISTORY = "alarm_history";
    public static final String ALARM_CYCLE = "alarm_cycle";
    public static final String AUXILARY = "auxilary";                       //告警附属属性表
    public static final String INFORM_RULE = "inform_rule";                  //告警投递表
    public static final String INFORM_RULE_USER = "inform_rule_user";       //通知规则，用户对应表
    public static final String INFORM_MSG = "inform_msg";                   //通知内容
    public static final String MSG_TEMPLATE = "msg_template";               //通知消息模板
}
