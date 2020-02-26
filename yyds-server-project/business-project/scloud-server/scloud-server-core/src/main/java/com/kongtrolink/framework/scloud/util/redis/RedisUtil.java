package com.kongtrolink.framework.scloud.util.redis;

/**
 * redis一些整理类
 */
public class RedisUtil {
    /**
     * 实时数据保存 企业编码+设备code
     * @param uniqueCode 企业编码
     * @param deviceCode 设备code
     * @return key
     */
    public static String getRealDataKey(String uniqueCode,String deviceCode){
        return  uniqueCode+"#"+ deviceCode;
    }
}
