package com.kongtrolink.framework.scloud.constant;

/**
 * @Auther: liudd
 * @Date: 2020/4/3 15:08
 * @Description:
 */
public class BaseConstant {

    public static String CHECKED = "已确认";
    public static String NOCHECK = "未确认";
    public static String UNDERLINE = "_";
    public static String COLON = ":";
    public static String LEDGE = "-";
    public static String TEMPLATE_MSG = "短信";
    public static String TEMPLATE_EMAIL = "邮件";
    public static String TEMPLATE_APP = "APP";
    public static String RESULT_SUCC = "成功";
    public static String RESULT_FAIL = "失败";
    public static String BASE_ALL = "全部";

    public static String ALARM_STATE_PENDING = "待处理";
    public static String ALARM_STATE_RESOLVE = "已消除";
    public static String ALARM_TYPE_CURRENT = "实时告警";
    public static String ALARM_TYPE_HISTORY = "历史告警";
    public static final String ALARM_OPERATE_RESOLVE = "告警消除";
    public static final String ALARM_OPERATE_CHECK = "告警确认";
    public static final String ALARM_OPERATE_NOTCHECK = "取消确认";
    public static final String ALARM_OPERATE_FOCUS = "告警关注";
    public static final String ALARM_OPERATE_NOFOCUS = "取消关注";

    public static final String REMOTE_OPERATE_SUCCKEYS = "succKeyList";
    public static final String REMOTE_OPERATE_FAILKEYS = "failKeyList";

    public static String OPERATE_ADD = "新增";
    public static String OPERATE_DELETE = "删除";
    public static String OPERATE_UPDATE = "修改";
    public static String OPERATE_USE = "启用";
    public static String OPERATE_FORBIT = "禁用";

    public static Integer JPUSH_TYPE_ALARM_REPORT = 101;
    public static Integer JPUSH_TYPE_ALARM_RECOVER = 102;
    public static Integer JPUSH_TYPE_NOTICE = 200;
    public static Integer JPUSH_TYPE_WORK = 300;
    public static Integer JPUSH_TYPE_EXAMINE = 400;
    public static Integer JPUSH_TYPE_REPAIRS = 500;
}
