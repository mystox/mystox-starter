package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignal;
import com.kongtrolink.framework.core.entity.EnumAlarmStatus;
import com.kongtrolink.framework.core.entity.RedisHashTable;
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
    private String  alarm_begin_delay_hash = RedisHashTable.SN_DEV_ID_ALARM_BEGIN_DELAY_HASH;

    /**
     * @auther: liudd
     * @date: 2019/4/12 17:07
     * 功能描述:处理告警
     */
    public Map<String, Object> analysisAlarm(JsonFsu fsu, Map<String, Float> dev_colId_valMap, Date curDate){
        Map<String, Object> alarmMap = new HashMap<>();
        //获取FSU下所有以前告警
        Object beforAlarmMapObj = redisUtils.hget(sn__alarm_hash, fsu.getSN());
        if(null != beforAlarmMapObj){
            JSONObject beforAlarmMapJson = JSONObject.parseObject(beforAlarmMapObj.toString());
            alarmMap = beforAlarmMapJson.getInnerMap();
        }
        //获取当前fsu下所有延迟开始告警第一次异常上报时间
        Map<String, Object> beginDelayAlarmMap = new HashMap<>();
        Object dbObj = redisUtils.hget(alarm_begin_delay_hash, fsu.getSN());
        if(null != dbObj){
            JSONObject dbJsonObj = JSONObject.parseObject(dbObj.toString());
            beginDelayAlarmMap = dbJsonObj.getInnerMap();
        }
        for(Map.Entry<String, Float> entry : dev_colId_valMap.entrySet()){
            handleSignal(entry.getKey(), entry.getValue(), alarmMap, curDate, beginDelayAlarmMap);
        }
        return alarmMap;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/12 17:07
     * 功能描述:处理信号点下的所有告警点
     */
    public void handleSignal(String sn_dev_colId, Float value, Map<String, Object> beforAlarmMap, Date curDate
                , Map<String, Object> beginDelayAlarmMap){
        Object alarmSignalObj = redisUtils.hget(sn_dev_id_alarmsignal_hash, sn_dev_colId);
        if(null == alarmSignalObj){
            return ;
        }
        List<AlarmSignal> alarmSignals = JSONArray.parseArray(alarmSignalObj.toString(), AlarmSignal.class);
        for (AlarmSignal alarmSignal : alarmSignals) {        //比较各个告警点
            if (!alarmSignal.getEnable()) {
                continue;//告警屏蔽
            }
            String keyAlarmId = sn_dev_colId + alarmSignal.getId();//sn_dev_colId_alarmId
            Object beforAlarmObj = beforAlarmMap.get(keyAlarmId);//获取原先的告警
            Alarm beforAlarm = (Alarm)beforAlarmObj;
            if (null == beforAlarmObj) {//进入开始告警逻辑
                beforAlarm = beginAlarm(value, alarmSignal, curDate);
                //处理高频过滤
                beforAlarm = highRateFilterService.checkAlarm(beforAlarm, alarmSignal, curDate);
                delayService.beginAlarmDelay(beforAlarm, alarmSignal, curDate, keyAlarmId, beginDelayAlarmMap);
            } else {              //进入恢复告警逻辑
                endAlarm(beforAlarm, value, alarmSignal, curDate);
            }
            if(null != beforAlarm){
                beforAlarmMap.put(keyAlarmId, beforAlarm);
            }
        }
        //更新信号点数据
        redisUtils.hset(sn_dev_id_alarmsignal_hash, sn_dev_colId, JSONArray.toJSONString(alarmSignals));
    }

    public static void main(String[] a){
        System.out.println("-----------");
        Map<String, Object> alarmMap = new HashMap<>();
        Object alarmObj = alarmMap.get("111");
        Alarm beforAlarm = (Alarm) alarmObj;
        System.out.println("alarmObj:" + alarmObj + "; beforAlarm: " + beforAlarm);
        Alarm newAlarm =(Alarm)alarmMap.get("0000000");
        System.out.println("newAlarm:" + newAlarm);
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
            //填充信号点告警产生延迟以及结束延迟，避免告警延迟时再次获取信号点
            alarm.setDelay(alarmSignal.getDelay());
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
            //填充信号点告警消除延迟以及结束延迟，避免告警延迟时再次获取信号点
            beforAlarm.setRecoverDelay(alarmSignal.getRecoverDelay());
            return beforAlarm;
        }
        return null;
    }
}
