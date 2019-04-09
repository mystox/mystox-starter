package com.kongtrolink.framework.core.entity;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 14:54
 * \* Description:
 * \
 */
public class PktType
{

    public final static String GET_DATA = "get_data";//获取监控点数据
    public final static String GET_FSU = "get_fsu";//获取FSU
    public final static String CHECK_FSU = "check_fsu";//检查绑定
    public final static String GET_FSU_STATUS = "get_fsu_status";//检查绑定
    public final static String SET_FSU = "set_fsu";//检查绑定
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
    public final static String CONNECT = "connect";//终端网关报文通讯
    public final static String CLEANUP = "cleanUp";//终端网关报文通讯
    public final static String REGISTRY = "registry";//SN上报注册
    public final static String REGISTRY_CNTB = "registry_cntb";//SN上报注册至外部业务平台
    public final static String FSU_REPORT = "fsu_report"; //FSU信息上报
    public final static String DEV_LIST = "dev_list"; //上报终端接入设备信息
    public final static String DATA_REPORT = "data_report"; //终端数据变化上报
    public final static String DATA_REGISTER = "data_register"; //数据注册
    public final static String ALARM_SAVE = "alarm_save"; //保存告警
    public final static String LOG_SAVE = "log_save"; //保存日志
    public final static String ALARM_REGISTER = "alarm_register"; //告警注册

}