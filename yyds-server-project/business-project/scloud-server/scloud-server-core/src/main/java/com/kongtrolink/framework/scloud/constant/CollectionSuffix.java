package com.kongtrolink.framework.scloud.constant;

/**
 * 数据库表名 常量类
 * Created by Eric on 2020/2/11.
 */
public class CollectionSuffix {

    // 企业设备信号映射字典表
    public static final String SIGNAL_TYPE = "_signalType";
    // 企业设备表
    public static final String DEVICE = "_device";
    // 企业设备特有属性
    public static final String DEVICE_SPECIAL_INFO = "_device_special_info";
    // 企业站点
    public static final String SITE = "_site";
    // 企业历史数据
    public static final String HISTORY = "_history";
    // FSU Device关系表
    public static final String FSU_DEVICE = "_fsu_device";
    // 系统用户表
    public static final String USER = "_user";
    // 维护用户表
    public static final String MAINTAINER = "_maintainer";
    // 用户管辖站点表
    public static final String USER_SITE = "_user_site";
    // 附件表
    public static final String ATTACHMENTS = "_attachments";
    // 综合机房 展现的信号点
    public static final String MULTIPLE_ROOM_SIGNAL ="room_signalType";
    // 综合机房 展现的信号点-单个企业单个设备自定义设置
    public static final String MULTIPLE_ROOM_SIGNAL_CONFIG ="_room_signal_config";
    // 组态场景信息
    public static final String CONFIGRATION_3D = "_config3d";
    // app组态坐标映射
    public static final String CONFIGRATION_APP = "_config_app";
    //实时告警业务信息表
    public static final String CUR_ALARM_BUSINESS = "_current_alarm_business";
    //历史告警业务信息表
    public static final String HIS_ALARM_BUSINESS = "_history_alarm_business";
    //告警重定义规则
    public static final String REDEDINE_RULE = "_redefine_rule";
    //被重定义的告警
    public static final String REDEDINE_ALARM = "_redefine_alarm";

    // 工单
    public static final String WORK = "_work";

    // 工程管理测试单
    public static final String PROJECT_ORDER = "_project_order";
    // 工程管理测试单操作记录
    public static final String PROJECT_ORDER_LOG = "_project_order_log";

}
