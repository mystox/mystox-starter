package com.kongtrolink.framework.service;

import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignal;
import com.kongtrolink.framework.core.entity.EnumAlarmStatus;
import com.kongtrolink.framework.jsonType.DelayAlarm;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * @auther: liudd
     * @date: 2019/4/10 14:10
     * 功能描述:产生告警延迟
     */
    public Alarm beginAlarmDelay(Alarm beforAlarm, AlarmSignal alarmSignal, Date curDate, String keyAlarmId,
                                 Map<String, Object> bdBeforMap, Map<String, Object> bdNewMap){
        //如果没有新告警产生，不管原来是否有告警产生延迟，直接消除redis中相应告警产生延迟信息
        if(null == beforAlarm){
            bdBeforMap.remove(keyAlarmId);
            return null;
        }
        Integer delay = alarmSignal.getDelay();
        if(delay == 0){        //如果该告警点没有告警产生延迟，直接返回
            bdBeforMap.remove(keyAlarmId);
            return beforAlarm;
        }
        Object bdAlarm = bdBeforMap.get(keyAlarmId);
        if(null == bdAlarm){//如果需要产生延时，并且是第一次上报，将该告警点值保存到redis中，不产生告警
            beforAlarm.setDelay(delay);
            bdNewMap.put(keyAlarmId, beforAlarm);
            return null;
        }
        Alarm delayAlarm = (Alarm) bdAlarm;
        boolean inTime = (curDate.getTime() - delayAlarm.getUpdateTime().getTime()) < delay*1000;
        if(!inTime) { //如果超出产生延迟时间，则产生告警，设置告警初次产生时间,删除redis中告警产生延迟信息
            beforAlarm.setUpdateTime(delayAlarm.getUpdateTime());
            beforAlarm.setValue(delayAlarm.getValue());
            bdBeforMap.remove(keyAlarmId);
            return beforAlarm;
        }
        return null;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/10 19:20
     * 功能描述:从redis告警开始延迟中，产生新的告警
     */
    public void beginDelay2NewAlarm(JsonFsu fsu, Map<String, Object> bdBeforMap, Date curDate){
        List<JsonDevice> data = fsu.getData();
        if(null == data){
            data = new ArrayList<>();
        }
        String sn = fsu.getSN();
        Map<String, JsonDevice> dev_id_deviceMap = new HashMap<>();
        for(JsonDevice device : data){
            dev_id_deviceMap.put(device.getDev(), device);
        }
        Iterator<Map.Entry<String, Object>> iterator = bdBeforMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            Alarm delayAlarm = (Alarm)next.getValue();
            //判断是否到了延迟告警时间
            Date updateTime = delayAlarm.getUpdateTime();
            int delay = delayAlarm.getDelay();
            boolean inTime = (curDate.getTime() - updateTime.getTime()) < (delay * 1000);
            if(inTime){
                continue;
            }
            String keyDev = fsnKey2DevKey(key);
            JsonDevice device = dev_id_deviceMap.get(keyDev);
            if(null == device){
                device = new JsonDevice();
                device.setDev(keyDev);
            }
            handleDev(sn, device, key, delayAlarm);
            dev_id_deviceMap.put(keyDev, device);
            //需要删除redis中告警延迟信息
            iterator.remove();
        }
        List<JsonDevice> newDeviceList = new ArrayList<>();
        newDeviceList.addAll(dev_id_deviceMap.values());
        fsu.setData(newDeviceList);
    }

    private void handleDev(String sn, JsonDevice device, String key, Alarm delayAlarm){
        List<JsonSignal> signalList = device.getSignalList();
        if(null == signalList){
            signalList = new ArrayList<>();
        }
        //获取当前实时告警中有的信号点
        Map<String, JsonSignal> signalid_signal_map = new HashMap<>();
        String dev = device.getDev();
        for(JsonSignal signal : signalList){
            signalid_signal_map.put(sn+dev+signal.getId(), signal);
        }
        String signalId = fsnKey2SignalId(key); //MINI210121000001_3-1_302001
        JsonSignal jsonSignal = signalid_signal_map.get(signalId);
        if(null == jsonSignal){
            jsonSignal = new JsonSignal();
            //如果当前实时告警没有该延迟告警所在信号点，则创建信号点
            jsonSignal.setId(signalId.substring(signalId.lastIndexOf("_") +1));
        }
        handleSignal(jsonSignal, key, delayAlarm);
        signalid_signal_map.put(signalId, jsonSignal);
        List<JsonSignal> newSignalList = new ArrayList<>();
        signalList.addAll(signalid_signal_map.values());
        device.setSignalList(newSignalList);
    }

    private void handleSignal(JsonSignal jsonSignal, String key, Alarm delayAlarm){
        Map<String, Alarm> alarmMap = jsonSignal.getAlarmMap();
        if(null == alarmMap){
            alarmMap = new HashMap<>();
        }
        alarmMap.put(key, delayAlarm);
        jsonSignal.setAlarmMap(alarmMap);
    }

    /**
     * @auther: liudd
     * @date: 2019/4/11 17:25
     * 功能描述:根据fsnkey获取设备key
     */
    private static String fsnKey2DevKey(String fskKey){//MINI210121000001_3-1_302001_300000
        if(StringUtils.isBlank(fskKey)){
            return null;
        }
        String substring = fskKey.substring(fskKey.indexOf("_")+1);
        return substring.substring(0, substring.indexOf("_"));
    }

    private static String fsnKey2SignalId(String fsnKey){
        if(StringUtils.isBlank(fsnKey)){
            return null;
        }
        return fsnKey.substring(0, fsnKey.lastIndexOf("_"));
    }

    /**
     * @auther: liudd
     * @date: 2019/4/10 14:10
     * 功能描述:消除告警延迟。最终如果告警消除延迟，只需要修改告警状态即可
     */
    public void endAlarmDelay(Alarm beforAlarm, AlarmSignal alarmSignal, Date curDate, String keyAlarmId,
                              Map<String, Object> edBeforMap, Map<String, Object> edNewMap){
        //如果告警本身没有消除，直接返回
        byte link = beforAlarm.getLink();
        if((link & EnumAlarmStatus.END.getValue()) == 0){
            return ;
        }
        Integer recoverDelay = alarmSignal.getRecoverDelay();
        if(0 == recoverDelay){//该信号点没有告警延迟，直接返回
            return ;
        }
        Object edAlarm = edBeforMap.get(keyAlarmId);
        long curDateTime = curDate.getTime();
        byte noEndLink = (byte)(link & 7);
        if(null == edAlarm){    //需要延迟
            edNewMap.put(keyAlarmId, new DelayAlarm(recoverDelay, curDate.getTime()));
            beforAlarm.setLink(noEndLink);
            return ;
        }
        //已经到达告警消除延迟时间，需要删除redis中延迟告警信息，并设置告警点延迟告警第一次时间为0
        DelayAlarm delayAlarm = (DelayAlarm) edAlarm;
        boolean inTime = (curDateTime - delayAlarm.getDelayFT()) < (recoverDelay * 1000);
        if(inTime){
            beforAlarm.setLink(noEndLink);
            beforAlarm.setUpdateTime(new Date(delayAlarm.getDelayFT()));
        }else{
            edBeforMap.remove(keyAlarmId);
        }
        return;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/11 15:12
     * 功能描述:未变量上报的告警消除延迟处理
     */
    public void endDelay2ResoveAlarm(JsonFsu fsu, Map<String, Object> edBeforMap, Date curDate){
        for(JsonDevice device : fsu.getData()){
            for(JsonSignal signal : device.getSignalList()){
                Map<String, Alarm> alarmMap = signal.getAlarmMap();
                for(String key : alarmMap.keySet()){
                    Object obj = edBeforMap.get(key);
                    if(null != obj){
                        DelayAlarm delayAlarm = (DelayAlarm)obj;
                        int recoverDelay = delayAlarm.getDelay();
                        Alarm alarm = alarmMap.get(key);
                        boolean inTime = (curDate.getTime() - delayAlarm.getDelayFT()) < (recoverDelay * 1000);
                        if(!inTime){
                            byte link = alarm.getLink();
                            link = (byte) (link & EnumAlarmStatus.END.getValue());
                            alarm.setLink(link);
                            edBeforMap.remove(key);
                        }
                    }
                }
            }
        }
    }
}
