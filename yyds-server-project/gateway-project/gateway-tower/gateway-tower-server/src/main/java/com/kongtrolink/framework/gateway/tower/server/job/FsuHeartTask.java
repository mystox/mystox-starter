package com.kongtrolink.framework.gateway.tower.server.job;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.gateway.tower.core.constant.GatewayTonerOperate;
import com.kongtrolink.framework.gateway.tower.core.constant.RedisKey;
import com.kongtrolink.framework.gateway.tower.core.entity.RedisFsuInfo;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.dto.HeartModuleDto;
import com.kongtrolink.framework.gateway.tower.core.util.RedisKeyUtil;
import com.kongtrolink.framework.gateway.tower.server.service.NorthService;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * FSU心跳检测
 */
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class FsuHeartTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(FsuHeartTask.class);

    @Autowired
    RedisUtils redisUtils;
    @Value("${gateway.enterpriseCode}")
    private String uniqueCode; //必须配置
    @Autowired
    MqttOpera mqttOpera;
    @Scheduled(cron = "${corn.heart:0 * * * * ?}")
    public void execute() {
        LOGGER.info("开始进行心跳检测...");
        Map<Object,Object> redisMap =  redisUtils.hmget(RedisKeyUtil.getRedisKey(uniqueCode,RedisKey.FSU_INFO));
        if(redisMap==null || redisMap.size()==0){
            LOGGER.info(" 无FSU需要 心跳检测 !! 结束");
            return;
        }
        for(Map.Entry<Object,Object> map : redisMap.entrySet()){
            //key : uniqueCode#fsuCode
            String redisKey = String.valueOf(map.getKey());
            try{
                RedisFsuInfo fsu = JSONObject.parseObject(String.valueOf(map.getValue()),RedisFsuInfo.class);
                HeartModuleDto dto = new HeartModuleDto();
                dto.setRedisFsuInfo(fsu);
                dto.setUniqueCode(uniqueCode);
                mqttOpera.opera(GatewayTonerOperate.HEART,JSONObject.toJSONString(dto));
            }catch (Exception e){
                e.printStackTrace();
                LOGGER.error("心跳 redisKey:{} 处理异常 ",redisKey);
            }
        }
    }
}