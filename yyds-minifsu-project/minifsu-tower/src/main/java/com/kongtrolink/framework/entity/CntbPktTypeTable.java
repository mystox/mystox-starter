package com.kongtrolink.framework.entity;

public class CntbPktTypeTable {
    //服务到网关报文
    public final static String SERVICE_GW = "service_gw";
    //网关到服务报文
    public final static String GW_SERVICE = "gw_service";

    public final static String LOGIN = "LOGIN";
    public final static int LOGIN_CODE = 101;
    public final static String LOGIN_ACK = "LOGIN_ACK";
    public final static int LOGIN_ACK_CODE = 102;

    public final static String GET_DATA = "GET_DATA";
    public final static int GET_DATA_CODE = 401;
    public final static String GET_DATA_ACK = "GET_DATA_ACK";
    public final static int GET_DATA_ACK_CODE = 402;

    public final static String SEND_ALARM = "SEND_ALARM";
    public final static int SEND_ALARM_CODE = 501;
    public final static String SEND_ALARM_ACK = "SEND_ALARM_ACK";
    public final static int SEND_ALARM_ACK_CODE = 502;

    public final static String SET_POINT = "SET_POINT";
    public final static int SET_POINT_CODE = 1001;
    public final static String SET_POINT_ACK = "SET_POINT_ACK";
    public final static int SET_POINT_ACK_CODE = 1002;

    public final static String TIME_CHECK = "TIME_CHECK";
    public final static int TIME_CHECK_CODE = 1301;
    public final static String TIME_CHECK_ACK = "TIME_CHECK_ACK";
    public final static int TIME_CHECK_ACK_CODE = 1302;

    public final static String GET_FSUINFO = "GET_FSUINFO";
    public final static int GET_FSUINFO_CODE = 1701;
    public final static String GET_FSUINFO_ACK = "GET_FSUINFO_ACK";
    public final static int GET_FSUINFO_ACK_CODE = 1702;

    public final static String GET_THRESHOLD = "GET_THRESHOLD";
    public final static int GET_THRESHOLD_CODE = 1901;
    public final static String GET_THRESHOLD_ACK = "GET_THRESHOLD_ACK";
    public final static int GET_THRESHOLD_ACK_CODE = 1902;

    public final static String SET_THRESHOLD = "SET_THRESHOLD";
    public final static int SET_THRESHOLD_CODE = 2001;
    public final static String SET_THRESHOLD_ACK = "SET_THRESHOLD_ACK";
    public final static int SET_THRESHOLD_ACK_CODE = 2002;
}
