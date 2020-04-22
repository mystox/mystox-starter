package com.kongtrolink.framework.scloud.constant;

/**
 * redis值
 * by Mag on 2019/8/12.
 */
public class RedisKey {
    /**
     * 保存实时数据Hash 格式
     * item： scloud_device_realData
     * key: fsuCode # device code
     * value：Map格式
     *      key：信号点的cntbId
     *      value：值
     */
    public static final String DEVICE_REAL_DATA = "scloud_device_realData";
    //保存下发值
    public static final String DEVICE_SET_POINT = "scloud_device_setPoint";
}
