package com.kongtrolink.framework.service;

import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignal;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
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
                                 JsonSignal signal, Map<String, Object> bdBeforMap, Map<String, Object> bdNewMap){
        if(null == beforAlarm){
            return null;
        }
        Integer delay = alarmSignal.getDelay();
        Long delayFT = alarmSignal.getDelayFT();
        boolean inTime = (curDate.getTime() - delayFT) < delay*1000;
        //如果没有告警产生延迟，直接返回
        if(delay == 0){
            return beforAlarm;
        }
        String bdKey = keyAlarmId.substring(keyAlarmId.indexOf("_")+1);
        if(0 == delayFT){   //如果需要产生延时，并且是第一次上报，将该告警点值保存到redis中，不产生告警
            bdNewMap.put(bdKey, beforAlarm);
            return null;
        }

        if(!inTime) { //如果超出产生延迟时间，则产生告警，修改alarmSignal中delayFT为0， 同时删除redis中对应记录
            //获取redis中第一次告警的值
            Object valueObj = bdBeforMap.get(bdKey);
            if(null != valueObj){
                beforAlarm = (Alarm) valueObj;
            }
            //删除redis中上次告警信息
            bdBeforMap.remove(bdKey);
            alarmSignal.setDelayFT(0L);
        }
        return beforAlarm;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/10 19:20
     * 功能描述:告警开始延迟中，产生新的告警
     */
    public void beginDelay2NewAlarm(JsonFsu fsu, Map<String, Object> bdBeforMap, Date curDate){
        List<JsonDevice> data = fsu.getData();
        if(null == data){
            data = new ArrayList<>();
        }
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
            String keyDev = key.substring(0, key.indexOf("_"));
            JsonDevice device = dev_id_deviceMap.get(keyDev);
            if(null == device){
                device = new JsonDevice();
                device.setDev(keyDev);
            }
            handleDev(fsu, device, key, delayAlarm);
            dev_id_deviceMap.put(keyDev, device);
            List<JsonDevice> newDeviceList = dev_id_deviceMap2DeeviceList(dev_id_deviceMap);
            fsu.setData(newDeviceList);
            iterator.remove();
        }
    }

    private void handleDev(JsonFsu fsu, JsonDevice device, String key, Alarm delayAlarm){
        List<JsonSignal> signalList = device.getSignalList();
        if(null == signalList){
            signalList = new ArrayList<>();
        }
        Map<String, JsonSignal> signalid_signal_map = new HashMap<>();
        for(JsonSignal signal : signalList){
            signalid_signal_map.put(signal.getId(), signal);
        }
        String signalId = key.substring(key.indexOf("_"), key.lastIndexOf("_"));
        JsonSignal jsonSignal = signalid_signal_map.get(signalId);
        if(null == jsonSignal){
            jsonSignal = new JsonSignal();
            jsonSignal.setId(signalId);
        }
        handleSignal(fsu, jsonSignal, key, delayAlarm);
        signalid_signal_map.put(signalId, jsonSignal);
        List<JsonSignal> newSignalList = signalId_Signal_Map2SignalList(signalid_signal_map);
        device.setSignalList(newSignalList);
    }

    private void handleSignal(JsonFsu fsu, JsonSignal jsonSignal, String key, Alarm delayAlarm){
        Map<String, Alarm> alarmMap = jsonSignal.getAlarmMap();
        if(null == alarmMap){
            alarmMap = new HashMap<>();
        }
        alarmMap.put(fsu.getSN()+"_" + key, delayAlarm);
        jsonSignal.setAlarmMap(alarmMap);
    }

    private List<JsonSignal> signalId_Signal_Map2SignalList(Map<String, JsonSignal> signalid_signal_map){
        List<JsonSignal> signalList = new ArrayList<>();
        signalList.addAll(signalid_signal_map.values());
        return signalList;
    }

    private List<JsonDevice> dev_id_deviceMap2DeeviceList(Map<String, JsonDevice> dev_id_deviceMap){
        List<JsonDevice> deviceList = new ArrayList<>();
        deviceList.addAll(dev_id_deviceMap.values());
        return deviceList;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/10 14:10
     * 功能描述:消除告警延迟
     */
    public void endAlarmDelay(Alarm beforAlarm, AlarmSignal alarmSignal, Date curDate){

    }
}
