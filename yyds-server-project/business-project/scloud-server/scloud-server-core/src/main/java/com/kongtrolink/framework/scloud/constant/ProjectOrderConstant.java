package com.kongtrolink.framework.scloud.constant;

/**
 * 工程管理测试单的状态
 * Created by Eric on 2020/4/14.
 */
public class ProjectOrderConstant {

    //-------------------测试单状态--------------------
    public static final String STATE_SUBMIT = "待提交";
    public static final String STATE_CHECK = "待审核";
    public static final String STATE_PASS = "已通过";
    public static final String STATE_CANCEL = "已作废";

    //-------------------操作动作--------------------
    public static final String ACTION_CREATE = "创建测试单";
    public static final String ACTION_SUBMIT = "提交测试单";
    public static final String ACTION_RETEST = "退回重测";
    public static final String ACTION_PASS = "通过";
    public static final String ACTION_CANCEL = "作废";

}
