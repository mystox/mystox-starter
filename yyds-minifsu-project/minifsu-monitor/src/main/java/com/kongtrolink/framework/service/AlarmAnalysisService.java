package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonFsu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2019/4/12 16:49
 * @Description:
 */
@Service
public class AlarmAnalysisService {
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    AlarmHighRateFilterService highRateFilterService;
    @Autowired
    AlarmDelayService delayService;

    private String sn_dev_id_alarmsignal_hash = RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH;
    private String sn__alarm_hash = RedisHashTable.SN_ALARM_HASH;

    /**
     * @auther: liudd
     * @date: 2019/4/12 17:07
     * 功能描述:处理告警
     */
    public Map<String, Object> analysisAlarm(JsonFsu fsu, Map<String, Float> dev_colId_valMap, Date curDate,
                                             Map<String, Object> delayAlarmMap){
        Map<String, Object> beforAlarmMap = new HashMap<>();
        //获取FSU下所有以前告警
//        Object beforAlarmMapObj = redisUtils.hget(sn__alarm_hash, fsu.getSN());
        Map<Object, Object> hmget = redisUtils.hmget(sn__alarm_hash + fsu.getSN());
        if(null != hmget){
            for(Map.Entry<Object, Object> curAlarm : hmget.entrySet()){
                beforAlarmMap.put(curAlarm.getKey().toString(), curAlarm.getValue());
            }
        }
        for(Map.Entry<String, Float> entry : dev_colId_valMap.entrySet()){
            beforAlarmMap = handleSignal(entry.getKey(), entry.getValue(), beforAlarmMap, curDate);
        }
        if(null != beforAlarmMap && !beforAlarmMap.isEmpty()){
            /*
                处理历史遗留问题：1，产生延迟，但是此次该信号点没有变化上报；2：消除延迟，此次信号点没有变化上报.
                主要是为了筛选需要注册的告警，注册完后，将真实告警和延迟（产生，消除）一起存入redis
             */
            beforAlarmMap = delayService.handleHistory(beforAlarmMap, curDate, delayAlarmMap);
            //告警管理过滤和告警组合

        }
        return beforAlarmMap;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/12 17:07
     * 功能描述:处理信号点下的所有告警点
     */
    public Map<String, Object> handleSignal(String sn_dev_colId, Float value, Map<String, Object> beforAlarmMap, Date curDate){
        Object alarmSignalObj = redisUtils.hget(sn_dev_id_alarmsignal_hash, sn_dev_colId);
        if(null == alarmSignalObj){
            return null;
        }
        Map<String, Object> alarmMap = new HashMap<>();
        List<AlarmSignal> alarmSignals = JSONArray.parseArray(alarmSignalObj.toString(), AlarmSignal.class);
        for (AlarmSignal alarmSignal : alarmSignals) {        //比较各个告警点
            if (!alarmSignal.getEnable()) {
                continue;//告警屏蔽
            }
            String keyAlarmId = sn_dev_colId + CoreConstant.LINE_CUT_OFF + alarmSignal.getAlarmId();//sn_dev_colId_alarmId
            Alarm beforAlarm = null;//获取原先的告警
            Object beforAlarmObj = beforAlarmMap.get(keyAlarmId);
            if(null != beforAlarmObj) {
                beforAlarm = JSONObject.parseObject(beforAlarmObj.toString(), Alarm.class);
            }
            if (null == beforAlarm) {//进入开始告警逻辑
                beforAlarm = beginAlarm(value, alarmSignal, curDate);
                //处理高频过滤
//                beforAlarm = highRateFilterService.checkAlarm(beforAlarm, alarmSignal, curDate);
                delayService.beginDelayAlarm(beforAlarm, alarmSignal, curDate);
            } else {              //进入恢复告警逻辑
                beforAlarm = endAlarm(beforAlarm, value, alarmSignal, curDate);
                beforAlarm = delayService.endDelayAlarm(beforAlarm, alarmSignal, curDate);
            }
            if(null != beforAlarm){
                alarmMap.put(keyAlarmId, beforAlarm);
            }
        }
        //更新信号点数据
        redisUtils.hset(sn_dev_id_alarmsignal_hash, sn_dev_colId, JSONArray.toJSONString(alarmSignals));
        return alarmMap;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/27 16:17
     * 功能描述:开始告警
     */
    private Alarm beginAlarm(float vallue, AlarmSignal alarmSignal, Date curDate){
        if( (alarmSignal.getThresholdFlag() == 1 && vallue >= alarmSignal.getThreshold() )
                || (alarmSignal.getThresholdFlag() == 0 && vallue <= alarmSignal.getThreshold()) ){
            //产生告警
            Alarm alarm = new Alarm();
            alarm.setLink((byte)1);
            alarm.setValue(vallue);
            alarm.setAlarmId(alarmSignal.getAlarmId());
            alarm.setUpdateTime(curDate);
            return alarm;
        }
        return null;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/27 16:17
     * 功能描述:结束告警逻辑
     * 如果结束告警上报铁塔未成功，此时继续来结束告警标志
     */
    private Alarm endAlarm(Alarm beforAlarm, float value, AlarmSignal alarmSignal, Date curDate){
        if( (alarmSignal.getThresholdFlag() ==  1 && value <= alarmSignal.getThreshold() )
                || (alarmSignal.getThresholdFlag() ==0 && value >= alarmSignal.getThreshold()) ){
            byte link = beforAlarm.getLink();
            link = (byte)(link | EnumAlarmStatus.END.getValue());
            beforAlarm.setLink(link);
            beforAlarm.setValue(value);
            beforAlarm.setUpdateTime(curDate);
//            //填充信号点告警消除延迟以及结束延迟，避免告警延迟时再次获取信号点
//            beforAlarm.setRecoverDelay(alarmSignal.getRecoverDelay());
        }
        return beforAlarm;
    }
}
