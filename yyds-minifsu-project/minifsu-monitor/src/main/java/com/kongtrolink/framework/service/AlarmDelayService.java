package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import com.kongtrolink.framework.core.entity.EnumAlarmStatus;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.DelayAlarm;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
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

    private String  alarm_begin_delay_hash = RedisHashTable.SN_DEV_ID_ALARM_BEGIN_DELAY_HASH;

    /**
     * @auther: liudd
     * @date: 2019/4/13 10:47
     * 功能描述:判断延迟产生告警是否到达时间
     */
    private boolean inTime(Alarm alarm, Date curDate){
        int delay = alarm.getDelay();
        long beginDelayFT = alarm.getBeginDelayFT();
        return (curDate.getTime() - beginDelayFT) < (delay*1000);
    }

    /**
     * @auther: liudd
     * @date: 2019/4/13 13:08
     * 功能描述:判断告警消除延迟是否到达时间
     */
    private boolean endInTime(Alarm alarm, Date curDate){
        int recoverDelay = alarm.getRecoverDelay();
        long recoverDelayFT = alarm.getRecoverDelayFT();
        return (curDate.getTime() - recoverDelayFT) < (recoverDelay * 1000);
    }

    /**
     * @auther: liudd
     * @date: 2019/4/13 11:01
     * 功能描述:告警产生时判定延迟告警
     */
    public Alarm beginDelayAlarm(Alarm beforAlarm, AlarmSignalConfig alarmSignal, Date curDate){
        if(null == beforAlarm){
            return null;
        }
        Integer delay = alarmSignal.getDelay();
        if(delay == 0){        //如果该告警点没有告警产生延迟，修改状态为真实产生
            byte link = beforAlarm.getLink();
            beforAlarm.setLink((byte)(link | EnumAlarmStatus.REALBEGIN.getValue()));
            return beforAlarm;
        }
        //填充信号点告警产生延迟以及结束延迟，避免告警延迟时再次获取信号点
        beforAlarm.setDelay(alarmSignal.getDelay());
        beforAlarm.setBeginDelayFT(curDate.getTime());
        return beforAlarm;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/13 11:13
     * 功能描述:告警消除时判定延迟告警消除
     * 如果在告警消除延迟期间，告警又产生了，咋办？
     */
    public Alarm endDelayAlarm(Alarm beforAlarm, AlarmSignalConfig alarmSignal, Date curDate) {
        byte link = beforAlarm.getLink();
        Integer recoverDelay = alarmSignal.getRecoverDelay();
        byte realEndLink = (byte) (link | EnumAlarmStatus.REALEND.getValue());
        if ((link & EnumAlarmStatus.END.getValue()) != 0) {      //告警消除
            //判断该告警是否有产生延迟
            if (inTime(beforAlarm, curDate)) {    //在告警产生延迟期间，告警需要消除，则直接将告警删除，不需要保存和注册
                return null;
            } else {
                if((link & EnumAlarmStatus.REALEND.getValue()) == 0){   //告警不是真是消除
                    if(recoverDelay == 0){      //告警点不需要消除延迟，直接修改状态为真实消除
                        beforAlarm.setLink(realEndLink);
                        return beforAlarm;
                    }
                    if(!endInTime(beforAlarm, curDate)){    //消除延时已经过期，直接修改状态为真实消除。否则不做任何修改
                        beforAlarm.setLink(realEndLink);
                    }else{
                        //填充信号点告警消除延迟以及结束延迟，避免告警延迟时再次获取信号点
                        beforAlarm.setRecoverDelay(alarmSignal.getRecoverDelay());
                        beforAlarm.setBeginDelayFT(curDate.getTime());
                    }
                    return beforAlarm;
                }else{//以前告警延迟已经真实消除，直接返回告警即可。当告警真实消除后，推送给铁塔失败，倒追redis中一直存在该告警
                    return beforAlarm;
                }
            }
        }
        //告警数据没有消除，本来此时需要判定告警延迟产生问题，移动到后面处理
        return beforAlarm;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/13 14:26
     * 功能描述:处理历史遗留问题.只有真实产生和真实消除的告警需要注册
     */
    public Map<String, Object> handleHistory(Map<String, Object> alarmMap, Date curDate, Map<String, Object> delayAlarmMap ){
        Map<String, Object> realAlarmMap = new HashMap<>();
        for(String key : alarmMap.keySet()){
            Alarm alarm = (Alarm)alarmMap.get(key);
            byte link = alarm.getLink();
            if( (link & EnumAlarmStatus.END.getValue()) != 0 ){     //收到告警消除数据，判定是否真实消除
                if( (link & EnumAlarmStatus.REALEND.getValue()) != 0 ){
                    realAlarmMap.put(key, alarm);
                }else{  //判断告警消除延期是否过期
                    if(endInTime(alarm, curDate)){
                        delayAlarmMap.put(key, alarm);
                    }else{
                        //修改状态
                        alarm.setLink((byte) (link | EnumAlarmStatus.REALEND.getValue()));
                        realAlarmMap.put(key, alarm);
                    }
                }
            }else{          //告警产生
                if((link & EnumAlarmStatus.REALBEGIN.getValue()) != 0){
                    realAlarmMap.put(key, alarm);
                }else{//需要判定产生延时是否过期
                    if(inTime(alarm, curDate)){
                        delayAlarmMap.put(key, alarm);
                    }else{
                        //修改状态
                        alarm.setLink((byte) (link | EnumAlarmStatus.REALBEGIN.getValue()));
                        realAlarmMap.put(key, alarm);
                    }
                }
            }
        }
        return realAlarmMap;
    }

}
