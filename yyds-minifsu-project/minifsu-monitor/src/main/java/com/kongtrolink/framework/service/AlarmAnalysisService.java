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
    private String begin_delay_alarm_hash = RedisHashTable.SN_BEGIN_DELAY_ALARM_HASH;
    private String alarm_num_hash = RedisHashTable.SN_ALARM_NUM_HASH;

    /**
     * @auther: liudd
     * @date: 2019/4/12 17:07
     * 功能描述:处理告警
     */
    public Map<String, JSONObject> analysisAlarm(JsonFsu fsu, Map<String, Integer> dev_colId_valMap, Date curDate){
        String beginDelayTable = begin_delay_alarm_hash + fsu.getSN();
        //获取FSU下所有以前告警
        Map<String, JSONObject> beforAlarmMap = redisUtils.hmget(sn__alarm_hash + fsu.getSN());
        //获取所有的告警开始延迟
        Map<String, JSONObject> beginDelayAlarmMap = redisUtils.hmget(beginDelayTable);//延迟产生或延迟消除的告警
        //处理上报的各个信号点告警数据
        for(Map.Entry<String, Integer> entry : dev_colId_valMap.entrySet()){
            handleSignal(fsu, entry, beforAlarmMap, beginDelayAlarmMap, curDate);
        }
        beforAlarmMap = delayService.handleEndDelayHistory(beforAlarmMap, curDate);
        Map<String, JSONObject> realBeginDelayAlarm = delayService.handleBeginDelayHistory(beforAlarmMap, beginDelayAlarmMap, curDate);
        for(String key : beginDelayAlarmMap.keySet()){//一个个删除，这里应该有办法
            redisUtils.hdel(beginDelayTable, key);
        }
        //保存告警产生延迟到redis中
        redisUtils.hmset(beginDelayTable, realBeginDelayAlarm);
        //返回当前的真实告警
        return beforAlarmMap;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/12 17:07
     * 功能描述:处理信号点下的所有告警点
     */
    public Map<String, JSONObject> handleSignal(JsonFsu fsu, Map.Entry<String, Integer> entry,
                         Map<String, JSONObject> beforAlarmMap, Map<String, JSONObject> beginDelayAlarmMap, Date curDate){
        String dev_colId = entry.getKey();
        Integer signalValue = entry.getValue();
        Object alarmSignalObj = redisUtils.hget(sn_dev_id_alarmsignal_hash, fsu.getSN() + "_" + dev_colId);
        if(null == alarmSignalObj){
            return null;
        }
        String dev = dev_colId.substring(0, dev_colId.indexOf("_"));
        List<AlarmSignalConfig> alarmSignals = JSONArray.parseArray(alarmSignalObj.toString(), AlarmSignalConfig.class);
        for (AlarmSignalConfig alarmSignal : alarmSignals) {        //比较各个告警点
            if (!alarmSignal.getEnable()) {
                continue;//告警屏蔽
            }
            float value = signalValue;
            //20190428修改keyAlarmId为dev_alarmId，去除信号点信息，为兼容后续组合和关联功能

            String keyAlarmId = dev + CoreConstant.LINE_CUT_OFF + alarmSignal.getAlarmId();//dev_colId_alarmId
            Object beforAlarmObj = beforAlarmMap.get(keyAlarmId);
            Object beginDelayAlarmObj = beginDelayAlarmMap.get(keyAlarmId);
            Integer thresholdBase = alarmSignal.getThresholdBase();
            if(null != thresholdBase){
                value = value / thresholdBase;
            }
            if(null == beforAlarmObj && null == beginDelayAlarmObj){
                //原告警列表中和开始延迟告警列表中都没有该信号点，生成新告警，并判定是否需要开始延时
                Alarm alarm = beginAlarm(value, alarmSignal, curDate);
                //高频过滤是否产生告警
                alarm = highRateFilterService.highRateAlarmCreate(fsu, alarm, alarmSignal, curDate, keyAlarmId);
                if(null== alarm){
                    continue ;
                }
                //liuddtodo:设置dev_colId，告警注册和保存历史告警时需要。但是保存在redis中的真实告警和延迟告警需要去除，后期优化节约空间
                alarm.setDev(dev);
                //填充告警序列号，虽然延迟告警也填充序列号，可能浪费序列号并且增加redis操作，但是代码可读性更高
                alarm.setNum((int)redisUtils.hincr(alarm_num_hash, fsu.getSN(), 1d));
                delayService.beginDelayAlarm(alarm, alarmSignal, curDate, beforAlarmMap, beginDelayAlarmMap, keyAlarmId);
            }else if(null != beforAlarmObj){
                //原来告警中有，则进入告警消除
                Alarm beforAlarm = JSONObject.parseObject(beforAlarmObj.toString(), Alarm.class);
                endAlarm(beforAlarm, value, alarmSignal, curDate);
                delayService.endDelayAlarm(beforAlarm, alarmSignal, curDate);
                beforAlarmMap.put(keyAlarmId, (JSONObject) JSONObject.toJSON(beforAlarm));
            }else if(null != beginDelayAlarmObj){
                if( (alarmSignal.getThresholdFlag() ==  1 && value < alarmSignal.getThreshold() )
                        || (alarmSignal.getThresholdFlag() ==0 && value > alarmSignal.getThreshold()) ){
                    //延迟产生过期后第一次数据如果是异常，则告警产生，否则同延迟产生时间内告警消除处理
                    redisUtils.hdel(begin_delay_alarm_hash+fsu.getSN(), keyAlarmId); //删除redis中延迟产生数据
                    highRateFilterService.reduceHighRateInfo(fsu.getSN(),  keyAlarmId);
                    //liuddtodo 后期需要修改redis中重复告警延时数据，和将关联或者组合期间消除的告警保存到数据库，状态为待定
                    beginDelayAlarmMap.remove(keyAlarmId);
                }
            }
        }
        return beforAlarmMap;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/27 16:17
     * 功能描述:开始告警
     */
    private Alarm beginAlarm(float vallue, AlarmSignalConfig alarmSignal, Date curDate){
        if( (alarmSignal.getThresholdFlag() == 1 && vallue >= alarmSignal.getThreshold() )
                || (alarmSignal.getThresholdFlag() == 0 && vallue <= alarmSignal.getThreshold()) ){
            //产生告警
            Alarm alarm = new Alarm();
            alarm.setLink((byte)1);
            alarm.setValue(vallue);
            alarm.setAlarmId(alarmSignal.getAlarmId());
            alarm.settReport(curDate);
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
    private Alarm endAlarm(Alarm beforAlarm, float value, AlarmSignalConfig alarmSignal, Date curDate){
        if( (alarmSignal.getThresholdFlag() ==  1 && value < alarmSignal.getThreshold())
                || (alarmSignal.getThresholdFlag() ==0 && value > alarmSignal.getThreshold()) ){
            byte link = beforAlarm.getLink();
            link = (byte)(link | EnumAlarmStatus.END.getValue());
            beforAlarm.setLink(link);
            beforAlarm.settRecover(curDate);
        }else{//告警延时消除期间，告警数据再次异常，则将告警开始状态
            beforAlarm.settRecover(null);
            beforAlarm.setRecoverDelay(null);
            beforAlarm.setRecoverDelayFT(null);
            byte link = beforAlarm.getLink();
            link = (byte) (link & 251);
            beforAlarm.setLink(link);
        }
        return beforAlarm;
    }
}
