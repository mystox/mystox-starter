package com.kongtrolink.framework.core.constant;

/**
 * MQTT的服务发送貌似不需要server了
 * 还是统一管理内部 operate 比较好
 */
public class ScloudBusinessOperate {
    public static final String LOGIN = "business_login";
    public static final String GET_DATA = "business_getData";
    public static final String SET_POINT = "business_setPoint";
    public static final String GET_THRESHOLD = "business_getThreshold";
    public static final String SET_THRESHOLD = "business_setThreshold";
    public static final String GET_FSUINFO = "business_getFsuInfo";
    public static final String REBOOT = "business_reboot";
    public static final String HISTORY = "business_history";
    public static final String UPDATE_FSU_POLLING = "business_updateFsuPolling";
}
