package com.kongtrolink.framework.core.entity;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 14:54
 * \* Description:
 * \
 */
public class PktType {
    public final static String GET_DATA = "get_data";//获取监控点数据
    public final static String GET_FSU = "get_fsu";//获取FSU
    public final static String CHECK_FSU = "check_fsu";//检查绑定
    public final static String GET_FSU_STATUS = "get_fsu_status";//检查绑定
    public final static String SET_FSU = "set_fsu";//设置和绑定终端
    public final static String SET_DATA = "set_data"; //设置监控点数据
    public final static String SET_DATA_TERMINAL = "set_data_terminal"; //设置监控点数据 -- > 终端
    public final static String GET_HISTORY_DATA = "get_hist_data"; //获取监控点历史数据
    public final static String UPGRADE = "upgrade"; //升级
    public final static String COMPILER = "compiler"; //下载编译文件
    public final static String GET_DEVICES = "get_devices";
    public final static String SET_DEVICES = "set_devices";
    public final static String DEL_DEVICE = "del_device";
    public final static String GET_ALARM_PARAM = "get_alarm_param";
    public final static String SET_ALARM_PARAM = "set_alarm_param"; //设置门限值
    public final static String SET_THRESHOLD_TERMINAL = "set_threshold_terminal"; //设置终端门限值
    public final static String GET_ALARMS = "get_alarms";
    public final static String GET_STATION = "get_station";
    public final static String SET_STATION = "set_station"; //设置绑定
    public final static String GET_DEVICE_STATUS = "get_device_status";
    public final static String GET_DEVICES_POINTS = "get_devices_points";
    public final static String GET_OP_LOG = "get_op_log";
    public final static String LOGOUT = "logout";
    public final static String CONNECT = "connect";//终端网关报文通讯
    public final static String CLEANUP = "cleanUp";//终端网关报文通讯
    public final static String REGISTRY_CNTB = "registry_cntb";//SN上报注册至外部业务平台
    public final static String FSU_BIND = "fsu_bind";//終端綁定SN
    public final static String DATA_REGISTER = "data_register"; //实时数据上报外部平台
    public final static String FILE_GET = "file_get"; //终端获取升级文件
    public final static String ALARM_SAVE = "alarm_save"; //保存告警
    public final static String LOG_SAVE = "log_save"; //保存日志
    public final static String ALARM_REGISTER = "alarm_register"; //告警注册
    public final static String TERMINAL_SAVE = "terminal_save"; //保存终端
    public final static String TERMINAL_REBOOT = "terminal_reboot"; //保存终端
    public final static String DATA_REPORT = "data_report"; //数据变化上报--外部
    public final static String DATA_CHANGE = "data_change"; //实时数据上报--外部
    public final static String DATA_STATUS = "data_status"; //往外部服务发送状态数据
    public final static String RUN_STATUS = "run_status"; //状态数据上报
    public final static String GET_RUNSTATE = "get_runstate"; //获取状态数据
    public final static String TERMINAL_LOG_SAVE = "terminal_log_save"; //终端报文保存
    public final static String GET_TERMINAL_LOG = "get_terminal_log"; //终端报文保存
    public final static String ALARM_MODEL_IMPORT = "alarm_model_import"; //导入告警模板
    public final static String SIGNAL_MODEL_IMPORT = "signal_model_import"; //导入信号点模板
    public final static String TERMINAL_STATUS = "terminal_status"; //终端状态获取
    public final static String TERMINAL_UNBIND = "terminal_unbind"; //终端解绑
    public final static String FSU_UNBIND = "fsu_unbind"; //终端解绑
    public final static String SET_TERMINAL = "set_terminal"; //终端配置
    public final static String TERMINAL_BIND = "terminal_bind"; //终端配置
    public final static String HEART = "heart"; //心跳
    public final static String GET_COMPILER_VERSION = "get_compiler_version"; //获取编译版本信息
    public final static String REGISTER_INFORM_ALARM = "register_inform_alarm"; //注册通知告警模块
//    public static final String MONITOR_TO_POWER_DATA = "monitor_to_power_data"; //monitor发往铁塔的实时数据
}