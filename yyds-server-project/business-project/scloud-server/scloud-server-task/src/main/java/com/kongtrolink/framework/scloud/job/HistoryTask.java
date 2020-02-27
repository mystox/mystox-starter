package com.kongtrolink.framework.scloud.job;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.gateway.tower.core.constant.RedisKey;
import com.kongtrolink.framework.gateway.tower.core.entity.RedisFsuInfo;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.dto.HeartModuleDto;
import com.kongtrolink.framework.mqtt.service.IMqttSender;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.service.MqttOpera;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * FSU 历史数据查询
 */
public class HistoryTask  implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryTask.class);
    @Value("${gateway.enterpriseCode}")
    private String uniqueCode; //必须配置
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    @Lazy
    MqttSender mqttSender;

    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String uniqueCode = dataMap.getString("uniqueCode");
        LOGGER.info("企业:{} 开始进行历史数据轮询...",uniqueCode);
        Map<Object,Object> redisMap =  redisUtils.hmget(RedisKey.FSU_INFO + "_" +uniqueCode);
        if(redisMap==null || redisMap.size()==0){
            LOGGER.info(" 无FSU需要 历史数据轮询 !! 结束");
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
                mqttSender.sendToMqtt(fsu.getGatewayServerCode(),ScloudBusinessOperate.HISTORY,JSONObject.toJSONString(dto));
            }catch (Exception e){
                e.printStackTrace();
                LOGGER.error("心跳 redisKey:{} 处理异常 ",redisKey);
            }
        }
    }


}
