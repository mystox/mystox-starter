package com.kongtrolink.gateway.nb.cmcc.util;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.gateway.nb.cmcc.entity.base.BaseAck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 功能描述: OneNet数据推送接收程序工具类。
 *
 * Created by Roy on 2017/5/17.
 *
 */
public class EntityUtil {

    private static Logger logger = LoggerFactory.getLogger(EntityUtil.class);

    public static <T> T  getEntity(BaseAck ack, Class<T> clazz) throws Exception {
        if(ack==null || ack.getData()==null){
            throw new Exception("报文为null ");
        }
        String data = JSONObject.toJSONString(ack.getData());
        T value = JSONObject.parseObject(data,clazz);
        return value;
    }

    public static <T> T  getEntityObject(Object ack,  Class<T> clazz) {
        String data = JSONObject.toJSONString(ack);
        T value = JSONObject.parseObject(data,clazz);
        return value;
    }

    public static <T> T  getEntityString(String ack,  Class<T> clazz) {
        T value = JSONObject.parseObject(ack,clazz);
        return value;
    }
}
