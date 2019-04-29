package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import com.kongtrolink.framework.core.entity.CoreConstant;
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
     * @date: 2019/4/16 20:22
     * 功能描述:高频过滤判断告警是否可以产生
     * 为了避免延迟告警生效时，第一次高频信息需要从alarmSignal中获取（拼接键字符串等过程），在这里处理高频信息。
     * 当延迟产生时间内，告警消除时，再降低高频信息
     */
    public Alarm highRateAlarmCreate(JsonFsu fsu, Alarm beforAlarm, AlarmSignalConfig alarmSignal, Date curDate, String keyAlarmId){
        if(null == beforAlarm){
            return null;
        }
        String sn = fsu.getSN();
        int highRateI = alarmSignal.getHighRateI();
        int highRateT = alarmSignal.getHighRateT();
        String highRateKey = highrate_hash + sn + ":" + keyAlarmId;
        double highRate = redisUtils.hincr(highRateKey, keyAlarmId, 1);
        if(highRate == 1){
            //如果高频信息为1，需要重新创建有效时间
            redisUtils.expire(highRateKey, highRateI);
            return beforAlarm;
        }
        if(highRate == highRateT){
            //设置高频
            beforAlarm.setHighRate((byte)1);
            return beforAlarm;
        }else if(highRate > highRateT){
            return null;
        }
        return beforAlarm;



//        JSONObject highRateObj = (JSONObject)redisUtils.hget(highrate_hash + sn, keyAlarmId);
//        if(highRateObj == null){
//            if(highRateT == 0){
//                return beforAlarm;
//            }
//            highRateObj = new JSONObject();
//            highRateObj.put("highRateC", 1);
//            highRateObj.put("highRateFT", curDate.getTime());
//            redisUtils.hset(highrate_hash + sn, keyAlarmId, highRateObj);
//            return  beforAlarm;
//        }else{
//            highRateFT = highRateObj.getLong("highRateFT");
//            highRateC = highRateObj.getInteger("highRateC");
//            long diff = curDate.getTime() - highRateFT;
//            boolean inTime = diff < highRateI*1000;
//            if(inTime){
//                if(highRateC >= highRateT){
//                    return null;
//                }
//                highRateObj.put("highRateC", ++highRateC);
//                redisUtils.hset(highrate_hash + sn, keyAlarmId, highRateObj);
//                if(highRateC == highRateT){
//                    //设置高频
//                    beforAlarm.setHighRate((byte)1);
//                }
//                return beforAlarm;
//            }else{
//                highRateObj.put("highRateC", 1);
//                highRateObj.put("highRateFT", curDate.getTime());
//                redisUtils.hset(highrate_hash + sn, keyAlarmId, highRateObj);
//                return beforAlarm;
//            }
//        }
    }

    /**
     * @auther: liudd
     * @date: 2019/4/16 21:21
     * 功能描述:减少高频过滤属性
     */
    public void reduceHighRateInfo(String sn, String keyAlarmId){
        String highRateKey = highrate_hash + sn + ":" + keyAlarmId;
        double hdecr = redisUtils.hdecr(highRateKey, keyAlarmId, 1);
        if(-1 == hdecr){
            redisUtils.hdel(highRateKey, keyAlarmId);
        }
//        JSONObject highRateObj = (JSONObject)redisUtils.hget(highRateKey, keyAlarmId);
//        if(null != highRateObj){
//            int highRateC = highRateObj.getInteger("highRateC");
//            if(1 == highRateC){
//                redisUtils.hdel(highrate_hash + sn, keyAlarmId);
//            }else{
//                highRateObj.put("highRateC", --highRateC);
//                redisUtils.hset(highrate_hash + sn, keyAlarmId, highRateObj);
//            }
//        }
    }

    /**
     * @auther: liudd
     * @date: 2019/4/16 21:21
     * 功能描述:增加高频过滤属性
     */
    public void increaseHighRateInfo(String sn, String keyAlarmId){
        JSONObject highRateObj = (JSONObject)redisUtils.hget(highrate_hash + sn, keyAlarmId);
        if(null == highRateObj){
            highRateObj = new JSONObject();
        }
        int highRateC = highRateObj.getInteger("highRateC");
        highRateObj.put("highRateC", ++highRateC);
        redisUtils.hset(highrate_hash + sn, keyAlarmId, highRateObj);
    }

    /**
     * @auther: liudd
     * @date: 2019/4/16 20:23
     * 功能描述:增加高频过滤属性值
     */
//    public Alarm updateHighRateInfo(String sn, Alarm beforAlarm, AlarmSignalConfig alarmSignal, Date curDate, String keyAlarmId){
//        int highRateI = alarmSignal.getHighRateI();
//        int highRateT = alarmSignal.getHighRateT();
//        long highRateFT ;
//        int highRateC ;
//        JSONObject highRateObj = (JSONObject)redisUtils.hget(highrate_hash + sn, keyAlarmId);
//        if(highRateObj == null){
//            if(highRateT == 0){
//                return beforAlarm;
//            }
//            highRateObj = new JSONObject();
//            highRateObj.put("highRateC", 1);
//            highRateObj.put("highRateFT", curDate.getTime());
//            redisUtils.hset(highrate_hash + sn, keyAlarmId, highRateObj);
//            return  beforAlarm;
//        }else{
//            highRateFT = highRateObj.getLong("highRateFT");
//            highRateC = highRateObj.getInteger("highRateC");
//            long diff = curDate.getTime() - highRateFT;
//            boolean inTime = diff < highRateI*1000;
//            if(inTime){
//                if(highRateC >= highRateT){
//                    return null;
//                }
//                highRateObj.put("highRateC", ++highRateC);
//                redisUtils.hset(highrate_hash + sn, keyAlarmId, highRateObj);
//                if(highRateC == highRateT){
//                    //设置高频
//                    beforAlarm.setDesc(CoreConstant.HITH_RATE_ALARM);
//                }
//                return beforAlarm;
//            }else{
//                highRateObj.put("highRateC", 1);
//                highRateObj.put("highRateFT", curDate.getTime());
//                redisUtils.hset(highrate_hash + sn, keyAlarmId, highRateObj);
//                return beforAlarm;
//            }
//        }
//    }
}
