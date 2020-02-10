package com.kongtrolink.framework.business.scloud.his.service;


import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MQTT 数据结构上报信息任务
 *
 */
@Service
public class HistoryService {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    MqttOpera mqttOpera;

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryService.class);


    public void heartBeatFsu(String message){
        try {
//            HeartModuleDto dto = JSONObject.parseObject(message,HeartModuleDto.class);

            LOGGER.info("开始轮询:fsu fsuShortCode:{} ID:{}  心跳检测...");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
