package com.kongtrolink.framework.gateway.tower.core.util;

/**
 * @author Mag
 **/
public class RedisKeyUtil {

    public static String getRedisKey(String uniqueCode, String redisKey){
        return redisKey+"_" + uniqueCode;
    }
}
