package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignal;
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
     * @date: 2019/4/13 10:27
     * 功能描述:解析告警产生延迟
     */
    public void handleBeginDelayAlarm(JsonFsu fsu, Map<String, Object> alarmMap, Date curDate){
        //获取当前fsu下所有延迟开始告警第一次异常上报时间
        Map<String, Object> beginDelayAlarmMap = new HashMap<>();
        Object dbObj = redisUtils.hget(alarm_begin_delay_hash, fsu.getSN());
        if(null != dbObj){
            JSONObject dbJsonObj = JSONObject.parseObject(dbObj.toString());
            beginDelayAlarmMap = dbJsonObj.getInnerMap();
        }
        //比对当前告警，如果延迟告警中含有该键，则从当前告警中移除
        Map<String, Object> realMap = new HashedMap();
        for(String key : alarmMap.keySet()){
            if(beginDelayAlarmMap.containsKey(key)){
                continue;
            }else{
                realMap.put(key, alarmMap.get(key));
            }
        }
        alarmMap = realMap;
        //遍历告警产生延迟，如果有到达延迟时间的告警，则从告警产生延迟中移除，并加入告警map中。
        Map<String, Object> realDelayMap = new HashMap<>();
        for(String key : beginDelayAlarmMap.keySet()){
            Alarm alarm = (Alarm)beginDelayAlarmMap.get(key);
            if(inTime(alarm, curDate)){
                realDelayMap.put(key, alarm);
            }else{
                alarmMap.put(key, alarm);
            }
        }
    }

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
     * @date: 2019/4/10 14:10
     * 功能描述:产生告警延迟
     */
    public Alarm beginAlarmDelay(Alarm beforAlarm, AlarmSignal alarmSignal, Date curDate, String keyAlarmId,
                                 Map<String, Object> beginDelayAlarmMap){
        //如果没有新告警产生，不管原来是否有告警产生延迟，直接消除redis中相应告警产生延迟信息
        if(null == beforAlarm){
            beginDelayAlarmMap.remove(keyAlarmId);
            return null;
        }
        Integer delay = alarmSignal.getDelay();
        if(delay == 0){        //如果该告警点没有告警产生延迟，直接返回
            beginDelayAlarmMap.remove(keyAlarmId);
            return beforAlarm;
        }
        Alarm delayAlarm = (Alarm)beginDelayAlarmMap.get(keyAlarmId);
        if(null == delayAlarm){//如果需要产生延时，并且是第一次上报，将该告警点值保存到redis中，不产生告警
            beforAlarm.setDelay(delay);
            beforAlarm.setBeginDelayFT(curDate.getTime());
            beginDelayAlarmMap.put(keyAlarmId, beforAlarm);
            return null;
        }
        boolean inTime = (curDate.getTime() - delayAlarm.getBeginDelayFT()) < delay*1000;
        if(!inTime) { //如果超出产生延迟时间，则产生告警，设置告警初次产生时间,删除redis中告警产生延迟信息
            beforAlarm.setUpdateTime(new Date(delayAlarm.getBeginDelayFT()));
            beforAlarm.setValue(delayAlarm.getValue());
            beginDelayAlarmMap.remove(keyAlarmId);
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

    /**
     * @auther: liudd
     * @date: 2019/4/13 11:01
     * 功能描述:告警产生时判定延迟告警
     */
    public Alarm beginDelayAlarm(Alarm beforAlarm, AlarmSignal alarmSignal, Date curDate){
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
        return null;
    }

    /**
     * @auther: liudd
     * @date: 2019/4/13 11:13
     * 功能描述:告警消除时判定延迟告警消除
     * 如果在告警消除延迟期间，告警又产生了，咋办？
     */
    public Alarm endDelayAlarm(Alarm beforAlarm, AlarmSignal alarmSignal, Date curDate) {
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
