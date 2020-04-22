package com.kongtrolink.framework.scloud.constant;

/**
 * 通用常量类
 * Created by Eric on 2020/2/12.
 */
public class CommonConstant {

    public static final String OFFLINE = "离线";
    public static final String ONLINE = "在线";
    public static final String YES = "是";
    public static final String NO = "否";
    public static final int FAILED = 0; //失败
    public static final int SUCCESSFUL = 1; //成功
    public static final String NOT_MODIFIED = "0";  //未修改
    public static final String MODIFIED = "1";  //已修改

    //--------------------搜索类型----------------------
    public static final int SEARCH_TYPE_FUZZY = 0;  //模糊搜索
    public static final int SEARCH_TYPE_EXACT = 1;  //精确搜索
    public static final int SEARCH_TYPE_IN = 2;  //IN搜索

    //--------------------连接类型----------------------
    public static final String RELATIONSHIP_TYPE_INSTALL = "Install";   //安装于

    //--------------------设备类型----------------------
    public static final String DEVICE_TYPE_FSU = "FSU动环主机";
    public static final String DEVICE_TYPE_CODE_FSU = "038";

    //--------------------角色----------------------
    public static final String ROLE_MAINTAINER = "维护人员";
}
