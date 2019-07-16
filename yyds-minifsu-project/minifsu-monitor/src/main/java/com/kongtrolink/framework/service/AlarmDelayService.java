package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2019/4/9 14:38
 * @Description:告警延迟service:告警产生延迟和恢复延迟（假设产生延迟和恢复延迟时间分别为5分钟和8分钟）
 *第一次产生，将告警点延迟开始时间设置为当前时间戳
 * 五分钟内告警消除，则将告警开始延迟时间设置为0，当做告警未产生过
 * 五分钟外，告警还是产生，则生成告警，同时将告警延迟时间设置为0，为下次告警开始延迟做准备
 */
@Service
public class AlarmDelayService {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    AlarmHighRateFilterService highRateFilterService;

    /**
     * @auther: liudd
     * @date: 2019/4/13 10:47
     * 功能描述:判断延迟产生告警是否到达时间
     */
    private boolean inTime(Alarm alarm, Date curDate){
        Integer delay = alarm.getDelay();
        Long beginDelayFT = alarm.getBeginDelayFT();
        if(null == delay || null == beginDelayFT){
            return false;
        }
        return (curDate.getTime() - beginDelayFT) < (delay*1000);
    }

    /**
     * @auther: liudd
     * @date: 2019/4/13 13:08
     * 功能描述:判断告警消除延迟是否到达时间
     */
    private boolean endInTime(Alarm alarm, Date curDate){
        Integer recoverDelay = alarm.getRecoverDelay();
        Long recoverDelayFT = alarm.getRecoverDelayFT();
        if(null == recoverDelay || null == recoverDelayFT){
            return false;
        }
        return (curDate.getTime() - recoverDelayFT) < (recoverDelay * 1000);
    }

    /**
     * @auther: liudd
     * @date: 2019/4/13 11:01
     * 功能描述:告警产生时判定延迟告警
     */
    public Alarm beginDelayAlarm(Alarm alarm, AlarmSignalConfig alarmSignal, Date curDate,
                 Map<String, JSONObject> beforAlarmMap, Map<String, JSONObject> beginDelayAlarmMap, String keyAlarmId){
        if(null == alarm){
            return null;
        }
        int delay = alarmSignal.getDelay();
        alarm.setDelay(delay);
        if(0 == delay){
            byte link = alarm.getLink();
            alarm.setLink((byte)(link | EnumAlarmStatus.REALBEGIN.getValue()));
//            beforAlarmMap.put(keyAlarmId, (JSONObject) JSONObject.toJSON(alarm));
//            return alarm;
        }else{
            alarm.setBeginDelayFT(curDate.getTime());
        }
        beginDelayAlarmMap.put(keyAlarmId, (JSONObject) JSONObject.toJSON(alarm));
        return  alarm;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/13 11:13
     * 功能描述:告警消除时判定延迟告警消除
     */
    public Alarm endDelayAlarm(Alarm beforAlarm, AlarmSignalConfig alarmSignal, Date curDate) {
        if(null == beforAlarm){
            return null;
        }
        byte link = beforAlarm.getLink();
        int recoverDelay = alarmSignal.getRecoverDelay();
        byte realEndLink = (byte) (link | EnumAlarmStatus.REALEND.getValue());
        if ((link & EnumAlarmStatus.END.getValue()) != 0) {      //告警消除

            if((link & EnumAlarmStatus.REALEND.getValue()) == 0){   //告警不是真是消除
                if(recoverDelay == 0){      //告警点不需要消除延迟，直接修改状态为真实消除
                    beforAlarm.setLink(realEndLink);
                    return beforAlarm;
                }
                if(null == beforAlarm.getRecoverDelayFT()){ /*填充信号点告警消除延迟以及结束延迟，避免告警延迟时再次获取信号点*/
                    beforAlarm.setRecoverDelay(alarmSignal.getRecoverDelay());
                    beforAlarm.setRecoverDelayFT(curDate.getTime());
                    return beforAlarm;
                }
                return beforAlarm;
            }
        }
        return beforAlarm;
    }

    /**
     * @auther: liudd
     * @date: 2019/5/24 9:07
     * 功能描述:统一处理产生延时的告警
     * 如果告警产生延时到期，转移到真是告警中
     */
    public Map<String, JSONObject> handleBeginDelayHistory(Map<String, JSONObject> alarmMap, Map<String,
            JSONObject> beginDelayAlarm, Map<String, Integer> DI_dev_colId_valMap, Date curDate){
        Map<String, JSONObject> realBeginDelayAlarm = new HashMap<>();
        for(String key : beginDelayAlarm.keySet()){
            Alarm alarm =  JSON.toJavaObject(beginDelayAlarm.get(key), Alarm.class);
            if(inTime(alarm, curDate)){
               realBeginDelayAlarm.put(key, (JSONObject)JSONObject.toJSON(alarm));
            }else{
                byte link = alarm.getLink();
                link = (byte)(link | EnumAlarmStatus.REALBEGIN.getValue());
                alarm.setLink(link);
                //修改产生时间为当前时间
                alarm.settReport(curDate);
                //去除产生延迟多余字段
                alarm.setDelay(null);
                alarm.setBeginDelayFT(null);
                alarmMap.put(key, (JSONObject)JSONObject.toJSON(alarm));
                //20190524 DI点处理
                if(EnumSignalColType.AO.equals(alarm.getCoType())){
                    DI_dev_colId_valMap.put(key, 1);
                }
            }
        }
        return realBeginDelayAlarm;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/16 14:33
     * 功能描述:处理告警中消除延迟到期的告警
     */
    public Map<String, JSONObject>  handleEndDelayHistory(Map<String, JSONObject> alarmMap, Map<String, Integer> DI_dev_colId_valMap, Date curDate){
        Map<String, JSONObject> realAlarmMap = new HashMap<>();
        for(String key : alarmMap.keySet()){
            JSONObject alarmObj = alarmMap.get(key);
            Alarm alarm  = JSONObject.parseObject(alarmObj.toString(), Alarm.class);
            byte link = alarm.getLink();
            int end = (link & EnumAlarmStatus.END.getValue());
            int realEnd = (link & EnumAlarmStatus.REALEND.getValue());
            if( (end != 0) && (realEnd == 0)){     //告警已消除，但不是真是消除
                //判断告警消除延期是否过期
                if(!endInTime(alarm, curDate)){
                    alarm.setLink((byte) (link | EnumAlarmStatus.REALEND.getValue()));
                    //修改消除时间为当前时间
                    alarm.settRecover(curDate);
                    //去除延迟消除等多余字段
                    alarm.setRecoverDelay(null);
                    alarm.setRecoverDelayFT(null);
                    //20190524 DI点处理
                    if(EnumSignalColType.AO.equals(alarm.getCoType())){
                        DI_dev_colId_valMap.put(key, 0);
                    }
                }
            }
            realAlarmMap.put(key, (JSONObject) JSONObject.toJSON(alarm));
        }
        return realAlarmMap;
    }
}
