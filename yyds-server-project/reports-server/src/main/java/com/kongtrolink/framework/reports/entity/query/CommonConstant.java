package com.kongtrolink.framework.reports.entity.query;

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

    //--------------------搜索类型----------------------
    public static final int SEARCH_TYPE_FUZZY = 0;  //模糊搜索
    public static final int SEARCH_TYPE_EXACT = 1;  //精确搜索
    public static final int SEARCH_TYPE_IN = 2;  //IN搜索

    //--------------------连接类型----------------------
    public static final String RELATIONSHIP_TYPE_INSTALL = "Install";   //安装于

}