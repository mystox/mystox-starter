package com.kongtrolink.framework.model;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 14:54
 * \* Description:
 * \
 */
public class PktType {

    public final static String GET_DATA = "get_data";//获取监控点数据
    public final static String SET_DATA = "set_data"; //设置监控点数据
    public final static String GET_HISTORY_DATA = "get_hist_data"; //获取监控点历史数据
    public final static String UPDATE = "update"; //升级
    public final static String COMPILER = "compiler";
    public final static String GET_DEVICES = "get_devices";
    public final static String SET_DEVICES = "set_devices";
    public final static String DEL_DEVICE = "del_device";
    public final static String GET_ALARM_PARAM = "get_alarm_param";
    public final static String SET_ALARM_PARAM = "set_alarm_param"; //设置门限值
    public final static String GET_ALARMS = "get_alarms";
    public final static String GET_STATION = "get_station";
    public final static String SET_STATION = "set_station";
    public final static String GET_DEVICE_STATUS = "get_device_status";
    public final static String GET_DEVICES_POINTS = "get_devices_points";
    public final static String GET_OP_LOG = "get_op_log";
    public final static String LOGOUT = "logout";



}