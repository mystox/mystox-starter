package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.AlarmSignal;
import com.kongtrolink.framework.core.entity.Communication;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2019/3/29 11:29
 * @Description:终端数据上报service
 */
@Service
public class DataReportService {

    @Autowired
    RedisUtils redisUtils;

    private String communication_hash = "";
    private  String sn_data_hash = "";
    private String sn_dev_id_alarmsignal_hash = "";
    private String sn__alarm_hash = "";

    public String report(String msgId, JSONObject payload, Date curDate){


        String result = "{'pktType':4,'result':1}";
        JsonFsu fsu = JSON.parseObject(payload.toJSONString(), JsonFsu.class);
        //判定redis中是否有该FSU的通讯信息
        String communicationStr= (String)redisUtils.hget(communication_hash, fsu.getSN());
        if(StringUtils.isBlank(communicationStr)){
            //写入日志，返回错误信息
            return  "{'pktType':4,'result':0}";
        }
        Communication communication = JSON.parseObject(communicationStr, Communication.class);
        if(communication.getStatus() == 0){     //终端未注册成功，返回注册失败消息
            return  "{'pktType':4,'result':0}";
        }
        //存储实时数据
        redisUtils.hset(sn_data_hash, fsu.getSN(), payload);
        //解析告警
        handlerAlarm(fsu, curDate);
        return result;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/28 19:27
     * 功能描述:获取FSU下所有告警
     */
    private HashMap<String, Alarm> fsu2AlarmMap(JsonFsu fsu){
        HashMap<String, Alarm> alarmMap = new HashMap<>();
        for(JsonDevice device : fsu.getData()){
            for(JsonSignal signal : device.getSignalList()){
                alarmMap.putAll(signal.getAlarmMap());
            }
        }
        return alarmMap;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/27 14:02
     * 功能描述:以device为单位处理告警
     */
    private void handlerAlarm(JsonFsu fsu, Date curDate){
        //获取FSU下所有告警
        String alarmFsuStr = (String)redisUtils.hget(sn__alarm_hash, fsu.getSN());
        HashMap<String, Alarm> alarmHashMap = new HashMap<>();
        if(!StringUtils.isBlank(alarmFsuStr)){
            JsonFsu alarmFsu = JSON.parseObject(alarmFsuStr, JsonFsu.class);
            alarmHashMap = fsu2AlarmMap(alarmFsu);
        }
        List<JsonDevice> alarmDeviceList = new ArrayList<>();
        for(JsonDevice device : fsu.getData()) {
            StringBuilder keyDev = new StringBuilder(fsu.getSN()).append(device.getDev());
            List<JsonSignal> alarmSignalList = new ArrayList<>();
            HashMap<String, Double> data = device.getData();
            for (String id : data.keySet()) {
                JsonSignal signal = new JsonSignal(id, data.get(id));
                handleSignal(signal, keyDev, alarmHashMap, curDate);
                if(signal.getAlarmMap() != null && !signal.getAlarmMap().isEmpty()){
                    signal.setId(null);
                    signal.setV(0.0);
                    alarmSignalList.add(signal);
                }
            }
            if(!alarmSignalList.isEmpty()) {
                device.setData(null);
                device.setSignalList(alarmSignalList);
                alarmDeviceList.add(device);
            }
        }
        fsu.setData(alarmDeviceList);
    }


    /**
     * @auther: liudd
     * @date: 2019/3/28 19:57
     * 功能描述:处理信号点下的告警信息
     */
    private void handleSignal(JsonSignal signal, StringBuilder keyDev,
                              HashMap<String, Alarm> alarmHashMap, Date curDate){
        StringBuilder keySignal = new StringBuilder(keyDev.toString()).append(signal);
        //获取redis中该信号点下所有告警点
        String redisSignalStr = (String) redisUtils.hget(sn_dev_id_alarmsignal_hash, keySignal.toString());
        if (StringUtils.isBlank(redisSignalStr)) {
            return ;//redis获取不到信号点，返回异常
        }
        JsonSignal jsonSignal = new JsonSignal();
        JsonSignal redisSignal = JSON.parseObject(redisSignalStr, JsonSignal.class);
        for (AlarmSignal alarmSignal : redisSignal.getAlarmSignals()) {        //比较各个告警点
            if (!alarmSignal.getEnable()) {
                continue;//告警屏蔽
            }
            String keyAlarmId = keySignal.append(alarmSignal.getAlarmId()).toString();
            Alarm beforAlarm = alarmHashMap.get(keyAlarmId);//获取原先的告警
            if (null == beforAlarm) {//进入开始告警逻辑
                beforAlarm = beginAlarm(signal, alarmSignal, curDate);
            } else {              //进入恢复告警逻辑
                endAlarm(beforAlarm, signal, alarmSignal, curDate);
            }
            if(null != beforAlarm){
                Map<String, Alarm> alarmMap = signal.getAlarmMap();
                if(null == alarmMap){alarmMap = new HashMap<>();}
                alarmMap.put(keyAlarmId, beforAlarm);
                signal.setAlarmMap(alarmMap);
            }
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/3/27 16:17
     * 功能描述:开始告警
     */
    private Alarm beginAlarm(JsonSignal signal, AlarmSignal alarmSignal, Date curDate){
        if( (alarmSignal.getThresholdFlag()>0 && signal.getV()>alarmSignal.getThreshold() )
                || (alarmSignal.getThresholdFlag()<0 && signal.getV()<alarmSignal.getThreshold()) ){
            //产生告警
            Alarm alarm = new Alarm();
            alarm.setHasBegin(true);
            alarm.setBeginReport(false);
            alarm.setHasEnd(false);
            alarm.setEndReort(false);
            alarm.setValue(signal.getV());
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
    private Alarm endAlarm(Alarm beforAlarm, JsonSignal signal, AlarmSignal alarmSignal, Date curDate){
        if( (alarmSignal.getThresholdFlag()>0 && signal.getV()<alarmSignal.getThreshold() )
                || ( alarmSignal.getThresholdFlag()<0 && signal.getV()>alarmSignal.getThreshold()) ){
            beforAlarm.setHasEnd(true);
            beforAlarm.setEndReort(false);
            beforAlarm.setUpdateTime(curDate);
        }
        return beforAlarm;
    }
}
