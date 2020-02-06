package com.kongtrolink.framework.gateway.tower.heart.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.gateway.tower.core.constant.GatewayTonerOperate;
import com.kongtrolink.framework.gateway.tower.core.constant.RedisKey;
import com.kongtrolink.framework.gateway.tower.core.entity.RedisFsuInfo;
import com.kongtrolink.framework.gateway.tower.core.entity.base.MessageResp;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.back.SendScMessage;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.dto.HeartModuleDto;
import com.kongtrolink.framework.gateway.tower.core.entity.mqtt.topic.EnumMqttSendSCTopics;
import com.kongtrolink.framework.gateway.tower.core.util.FSUServiceUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MQTT 数据结构上报信息任务
 *
 */
@Service
public class FsuHeartService {


    @Value("${gateway.fsuPort}")
    private int defaultFsuPort;
    @Value("${gateway.offlineNum}")
    private int offlineNum;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    MqttOpera mqttOpera;

    private static final Logger LOGGER = LoggerFactory.getLogger(FsuHeartService.class);


    public void heartBeatFsu(String message){
        try {
            HeartModuleDto dto = JSONObject.parseObject(message,HeartModuleDto.class);
            RedisFsuInfo fsu = dto.getRedisFsuInfo();
            String uniqueCode = dto.getUniqueCode();
            String fsuShortCode = fsu.getShortCode();
            //发送FSU心跳检测消息
            boolean flag = false;
            LOGGER.info("开始轮询:fsu fsuShortCode:{} ID:{}  心跳检测...",fsuShortCode,fsu.getId());
            try{
                MessageResp value = FSUServiceUtil.getFsuInfo(fsuShortCode,fsu.getIp(),defaultFsuPort);
                //有返回 证明 心跳正常
                if(value !=null && value.getInfo() !=null){
                    flag = true;
                }
            } catch (Exception e) {
                LOGGER.error(">>> 心跳检测服务 GET_FSUINFO=> fsuShortCode:{} 获取数据 异常  ERROR !!!!",fsuShortCode);
                e.printStackTrace();
            }
            //FSU离线次数redis保存
            String redisKeyFsuInfo = uniqueCode+"#"+fsuShortCode;
            if(!flag){
                //获取FSU离线消息
                Object offInfo = redisUtils.hget(RedisKey.FSU_HEART_OFF,redisKeyFsuInfo);
                int offlineNum =0;
                try{
                    offlineNum = Integer.parseInt(String.valueOf(offInfo));
                }catch (Exception e){
                    LOGGER.error("redis:{} 获取redisKey值:{} 异常",RedisKey.FSU_HEART_OFF,redisKeyFsuInfo);
                }
                offlineNum +=1;
                redisUtils.hset(RedisKey.FSU_HEART_OFF,redisKeyFsuInfo,offlineNum);
                boolean isAlarmReport = isOutAlarm(fsu,offlineNum);
                /**
                 * 判断已经离线了 进行2步骤（无先后顺序）
                 * 1 上报给告警模块
                 * 2 发送业务平台离线通知
                 * 当前貌似广播 所以业务平台也订阅GatewayTonerOperate.ALARM_OFFLINE即可
                 */
                if (isAlarmReport) {
                    //若发生离线告警 则将FSU移除redis队列，不再进行Task的相关定时任务
                    redisUtils.hdel(RedisKey.FSU_INFO,redisKeyFsuInfo);
                    redisUtils.hdel(RedisKey.FSU_HEART_OFF,redisKeyFsuInfo);
                    LOGGER.info("fsu {}  {} 离线超时 需要 进行离线告警上报 ", fsu.getId(),fsu.getShortCode());
                    //1 上报给告警模块
                    mqttOpera.opera(GatewayTonerOperate.ALARM_OFFLINE,fsu.getShortCode());
                }

            }else{
                //若在线是将离线次数 设置成0
                redisUtils.hset(RedisKey.FSU_HEART_OFF,redisKeyFsuInfo,String.valueOf(0));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 是否 进行告警上报
     */
    private boolean isOutAlarm(RedisFsuInfo fsu,int offNum) {
        LOGGER.info("FSU shortCode:{}  当前累计离线次数:{} 离线告警阈值:{}", fsu.getShortCode(),offNum,offlineNum);
            //如果 FSU 离线次数是否超过限值
        return  offNum > offlineNum;
    }
}
