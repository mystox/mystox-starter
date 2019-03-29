package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.core.entity.*;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.*;
import com.kongtrolink.framework.core.utils.RedisUtils;

/**
 * Created by mystoxlol on 2019/2/25, 19:25.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ExecuteModule extends RpcNotifyImpl implements ModuleInterface {

    @Autowired
    private RedisUtils redisUtil;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String communication_hash = "";
    private  String sn_data_hash = "";
    private String sn_dev_id_alarmsignal_hash = "";
    private String sn__alarm_hash = "";

    @Override
    public boolean init(){
        logger.info("workerExecute-execute module init");
        //初始化线程池之类的基础任务服务
        //加载告警组合，关联，过滤，延迟等配置信息放到redis中
        //可能还需要加载redis宕机后，数据库中的实时告警信息，如果需要判定信号点AI,DI类型，则需要再告警点中存储
        return true;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/26 13:09
     * 功能描述:实时数据处理模块，暂时不使用线程池
     *      1，直接将实时数据json串更新到redis中作为实时数据，供门户端获取站点
     *      2，解析实时数据，翻译告警数据并更新到redis中
     */
    @Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload){
        Date curDate = new Date();
        //1,将实时数据更新到reids中
        //2，解析
        RpcNotifyProto.MessageType response = RpcNotifyProto.MessageType.RESPONSE;
        String result = "{'pktType':4,'result':1}";
        Fsu fsu = JSON.parseObject(payload, Fsu.class);
         //判定redis中是否有该FSU的通讯信息
        String communicationStr= (String)redisUtil.hget(communication_hash, fsu.getSn());
        if(StringUtils.isBlank(communicationStr)){
            //写入日志，返回错误信息
            return createResp(response, "{'pktType':4,'result':0}", StringUtils.isBlank(msgId)?"":msgId);
        }
        Communication communication = JSON.parseObject(communicationStr, Communication.class);
        if(communication.getStatus() == 0){     //终端未注册成功，返回注册失败消息
            return createResp(response, "{'pktType':4,'result':0}", StringUtils.isBlank(msgId)?"":msgId);
        }
        //发送心跳
        sendHeartBeat(communication);
        //存储实时数据
        redisUtil.hset(sn_data_hash, fsu.getSn(), payload);
        //解析告警
        handlerAlarm(fsu, curDate);
        //发送告警信息给外部
        sendAlarmInfo(fsu);
        //返回消息
        return createResp(response, result, StringUtils.isBlank(msgId)?"":msgId);
    }

    /**
     * @auther: liudd
     * @date: 2019/3/27 14:02
     * 功能描述:以device为单位处理告警
     */
    private void handlerAlarm(Fsu fsu, Date curDate){
        //获取FSU下所有告警
        String alarmFsuStr = (String)redisUtil.hget(sn__alarm_hash, fsu.getSn());
        HashMap<String, Alarm> alarmHashMap = new HashMap<>();
        if(!StringUtils.isBlank(alarmFsuStr)){
            Fsu alarmFsu = JSON.parseObject(alarmFsuStr, Fsu.class);
            alarmHashMap = fsu2AlarmMap(alarmFsu);
        }
        List<Device> alarmDeviceList = new ArrayList<>();
        for(Device device : fsu.getDeviceList()) {
            StringBuilder keyDev = new StringBuilder(fsu.getSn()).append(device.getDev());
            List<Signal> alarmSignalList = new ArrayList<>();
            for (Signal signal : device.getSignalList()) {
                handleSignal(signal, keyDev, alarmHashMap, curDate);
                if(signal.getAlarmMap() != null && !signal.getAlarmMap().isEmpty()){
                    signal.setId(null);
                    signal.setV(0.0);
                    alarmSignalList.add(signal);
                }
            }
            if(!alarmSignalList.isEmpty()) {
                device.setSignalList(alarmSignalList);
                alarmDeviceList.add(device);
            }
        }
        fsu.setDeviceList(alarmDeviceList);
    }

    /**
     * @auther: liudd
     * @date: 2019/3/28 19:57
     * 功能描述:处理信号点下的告警信息
     */
    private void handleSignal(Signal signal, StringBuilder keyDev,
                              HashMap<String, Alarm> alarmHashMap, Date curDate){
        StringBuilder keySignal = new StringBuilder(keyDev.toString()).append(signal.getId());
        //获取redis中该信号点下所有告警点
        String redisSignalStr = (String) redisUtil.hget(sn_dev_id_alarmsignal_hash, keySignal.toString());
        if (StringUtils.isBlank(redisSignalStr)) {
            return;//redis获取不到信号点，返回异常
        }
        Signal redisSignal = JSON.parseObject(redisSignalStr, Signal.class);
        for (AlarmSignal alarmSignal : redisSignal.getAlarmSignals()) {        //比较各个告警点
            if (!alarmSignal.isEnable()) {
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
     * @date: 2019/3/28 19:27
     * 功能描述:获取FSU下所有告警
     */
    private HashMap<String, Alarm> fsu2AlarmMap(Fsu fsu){
        HashMap<String, Alarm> alarmMap = new HashMap<>();
        for(Device device : fsu.getDeviceList()){
            for(Signal signal : device.getSignalList()){
                alarmMap.putAll(signal.getAlarmMap());
            }
        }
        return alarmMap;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/27 16:17
     * 功能描述:开始告警
     */
    private Alarm beginAlarm(Signal signal, AlarmSignal alarmSignal, Date curDate){
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
    private Alarm endAlarm(Alarm beforAlarm, Signal signal, AlarmSignal alarmSignal, Date curDate){
        if( (alarmSignal.getThresholdFlag()>0 && signal.getV()<alarmSignal.getThreshold() )
            || ( alarmSignal.getThresholdFlag()<0 && signal.getV()>alarmSignal.getThreshold()) ){
            beforAlarm.setHasEnd(true);
            beforAlarm.setEndReort(false);
            beforAlarm.setUpdateTime(curDate);
        }
        return beforAlarm;
    }

    /**
     * @auther: liudd
     * @date: 2019/3/28 16:38-
     * 功能描述:发送心跳
     */
    private void sendHeartBeat(Communication communication){

    }

    /**
     * @auther: liudd
     * @date: 2019/3/28 20:02
     * 功能描述:发送告警信息
     */
    private void sendAlarmInfo(Fsu fsu){
        if(!fsu.getDeviceList().isEmpty()){

        }
    }

    private RpcNotifyProto.RpcMessage createResp(RpcNotifyProto.MessageType responseType, String result, String msgId){
        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(responseType).setPayload(result)
                .setMsgId(StringUtils.isBlank(msgId) ? "" : msgId).build();
    }
}
