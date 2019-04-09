package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.jsonType.JsonFsu;
import com.kongtrolink.framework.jsonType.JsonSignal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    private String sn_dev_id_alarmsignal_hash = RedisHashTable.SN_DEV_ID_ALARMSIGNAL_HASH;
    private String sn__alarm_hash = RedisHashTable.SN_ALARM_HASH;
    @Value("server.bindIp")
    private String serverIp;

    public String report(String msgId, JsonFsu fsu, Date curDate){
        String result = "{'pktType':4,'result':1}";
        //解析告警，这里可能需要定义异常，比如信号点不存在
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
            StringBuilder keyDev = new StringBuilder(fsu.getSN()).append("_").append(device.getDev());
            List<JsonSignal> alarmSignalList = new ArrayList<>();
            HashMap<String, Double> data = device.getInfo();
            for (String id : data.keySet()) {
                JsonSignal signal = new JsonSignal(id, data.get(id));
                handleSignal(signal, keyDev, alarmHashMap, curDate);
                if(signal.getAlarmMap() != null && !signal.getAlarmMap().isEmpty()){
                    signal.setV(null);
                    alarmSignalList.add(signal);
                }
            }
            if(!alarmSignalList.isEmpty()) {
                device.setInfo(null);
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
        StringBuilder keySignal = new StringBuilder(keyDev.toString())
                .append("_").append(signal.getId());
        //获取redis中该信号点下所有告警点
        Object redisSignalObj = redisUtils.hget(sn_dev_id_alarmsignal_hash, keySignal.toString());
        if (null == redisSignalObj) {
            return ;//redis获取不到信号点，返回异常
        }
        List<AlarmSignal> alarmSignals = JSONArray.parseArray(redisSignalObj.toString(), AlarmSignal.class);
        for (AlarmSignal alarmSignal : alarmSignals) {        //比较各个告警点
            if (!alarmSignal.getEnable()) {
                continue;//告警屏蔽
            }
            String keyAlarmId = keySignal.append("_").append(alarmSignal.getAlarmId()).toString();
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
        if( (alarmSignal.getThresholdFlag()>0 && signal.getV()>=alarmSignal.getThreshold() )
                || (alarmSignal.getThresholdFlag()<0 && signal.getV()<=alarmSignal.getThreshold()) ){
            //产生告警
            Alarm alarm = new Alarm();
            alarm.setLink((byte)1);
            alarm.setValue(signal.getV());
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
    private Alarm endAlarm(Alarm beforAlarm, JsonSignal signal, AlarmSignal alarmSignal, Date curDate){
        if( (alarmSignal.getThresholdFlag()>0 && signal.getV()<=alarmSignal.getThreshold() )
                || ( alarmSignal.getThresholdFlag()<0 && signal.getV()>=alarmSignal.getThreshold()) ){
            byte link = beforAlarm.getLink();
            link = (byte)(link | EnumAlarmStatus.END.getValue());
            beforAlarm.setLink(link);
            beforAlarm.setValue(signal.getV());
            beforAlarm.setUpdateTime(curDate);
            return beforAlarm;
        }
        return null;
    }
}
