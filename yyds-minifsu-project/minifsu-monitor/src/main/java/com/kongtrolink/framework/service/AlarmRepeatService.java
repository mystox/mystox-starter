package com.kongtrolink.framework.service;

import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonFsu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2019/5/5 09:00
 * @Description:重复延时告警
 */
@Service
public class AlarmRepeatService {

    @Autowired
    RedisUtils redisUtils;
    private String repeat_hash = RedisHashTable.REPEAT_SN_HASH;

    public Alarm handleAlarm(JsonFsu fsu, Alarm beforAlarm, AlarmSignalConfig signalConfig, String keyAlarmId){
        if(null == beforAlarm){
            return null;
        }
        String sn = fsu.getSN();
        Integer repeatDelay = signalConfig.getRepeatDelay();
        String repeatKey = repeat_hash + sn + ":" + keyAlarmId;
        double repeatCount = redisUtils.hincr(repeatKey, keyAlarmId, 1);
        if(1 == repeatCount){
            //如果为1，说明该告警没有重复延时，需要设置redis该键的有效时间
            redisUtils.expire(repeatKey, repeatDelay);
            return beforAlarm;
        }
        //如果不是1，说明已经重复延时了，返回空
        return null;
    }

    /**
     * @auther: liudd
     * @date: 2019/5/5 9:17
     * 功能描述:删除redis中重复延时信息
     */
    public void reduceRepeatInfo(String sn, String keyAlarmId){
        String repeatKey = repeat_hash + sn + ":" + keyAlarmId;
        redisUtils.hdel(repeatKey, keyAlarmId);
    }
}
