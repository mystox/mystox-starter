package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonFsu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/4/10 10:07
 * @Description:高频过滤service
 * 规则：
 * 初次产生告警，将告警点的highrateft设置为当前时间戳，highratec设置为1
 * 下次产生告警后，判断第一次告警产生时间差，
 *      如果属于规定时间段
 *          如果小于规定次数，则生成告警并更新highratec
 *          如果大于大于规定次数，则跳过
 *      如果在规定时间段外
 *          更新highrateft和重置highratec为1
 *
 */
@Service
public class AlarmHighRateFilterService {

    @Autowired
    RedisUtils redisUtils;

    private String highrate_hash = RedisHashTable.HIGHRATE_SN_HASH;
    /**
     * @auther: liudd
     * @date: 2019/4/10 11:26
     * 功能描述:告警产生判定高频过滤
     */
    public Alarm checkAlarm(JsonFsu fsu, Alarm beforAlarm, AlarmSignalConfig alarmSignal, Date curDate, String keyAlarmId){
        if(null == beforAlarm){
            return null;
        }
        int highRateI = alarmSignal.getHighRateI();
        int highRateT = alarmSignal.getHighRateT();
        long highRateFT ;
        int highRateC ;
        Object highRateObj = redisUtils.hget(highrate_hash + fsu.getSN(), keyAlarmId);
        JSONObject jsonObject ;
        if(highRateObj == null){
            if(highRateT == 0){
                return beforAlarm;
            }
            jsonObject = new JSONObject();
            jsonObject.put("highRateC", 1);
            jsonObject.put("highRateFT", curDate.getTime());
            redisUtils.hset(highrate_hash + fsu.getSN(), keyAlarmId, jsonObject);
            return  beforAlarm;
        }else{
            jsonObject = JSONObject.parseObject(highRateObj.toString());
            highRateFT = jsonObject.getLong("highRateFT");
            highRateC = jsonObject.getInteger("highRateC");
            long diff = curDate.getTime() - highRateFT;
            boolean inTime = diff < highRateI*1000;
            if(inTime){
                if(highRateC >= highRateT){
                    return null;
                }
                jsonObject.put("highRateC", highRateC+1);
                redisUtils.hset(highrate_hash + fsu.getSN(), keyAlarmId, jsonObject);
                return beforAlarm;
            }else{
                jsonObject.put("highRateC", 1);
                jsonObject.put("highRateFT", curDate.getTime());
                redisUtils.hset(highrate_hash + fsu.getSN(), keyAlarmId, jsonObject);
                return beforAlarm;
            }
        }
    }
}
