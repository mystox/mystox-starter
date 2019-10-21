package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.EnumLevelName;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.mqtt.AlarmEntrance;
import com.kongtrolink.framework.mqtt.AlarmMqttEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Auther: liudd
 * @Date: 2019/10/12 15:11
 * @Description:
 */
@Service
public class AlarmEntranceImpl implements AlarmEntrance {

    private String currentAlarmTable = MongTable.ALARM_CURRENT;
    private String historyAlarmTable = MongTable.ALARM_HISTORY;
    private static ConcurrentLinkedQueue<Alarm> recoverAndAuxilaryAlarmQueue = new ConcurrentLinkedQueue<>();
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    AlarmDao alarmDao;
    /**
     * @auther: liudd
     * @date: 2019/10/14 8:50
     * 功能描述:告警上报，如果已存在未消除告警，则跳过不处理
     * 1,实时告警表
     * 2，历史告警表
     *  告警消除和属性修改，可能需要使用队列来不断循环处理
     */
    @Override
    public void alarmHandle(String payload) {
        AlarmMqttEntity mqttEntity = JSONObject.parseObject(payload, AlarmMqttEntity.class);
        List<Alarm> alarmList = mqttEntity.getAlarms();
        List<Alarm> reportAlarmList = new ArrayList<>();
        if(null == alarmList || alarmList.size() == 0){
            System.out.printf("产生告警为空: %s %n ", payload);
            return ;
        }
        for(Alarm alarm : alarmList) {
            //初始化片键
            alarm.initSliceKey();
            alarm.setEnterpriseCode(mqttEntity.getEnterpriseCode());
            alarm.setServerCode(mqttEntity.getServerCode());
            String flag = alarm.getFlag();
            if(Contant.ONE.equals(flag)){   //告警上报
                Alarm report = report(alarm);
                if(null != report){
                    reportAlarmList.add(alarm);
                }
            }else {
                recoverAndAuxilaryAlarmQueue.add(alarm);
                taskExecutor.execute(()->handleRecoverAndAuxilary());
            }

            //保存实时
            if(reportAlarmList.size() != 0) {
                //liuddtodo 按照配置文件，调用各个服务
                alarmDao.save(reportAlarmList, currentAlarmTable);
            }
        }
        return ;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/21 9:39
     * 功能描述:告警产生
     * 1，判断实时告警表和历史告警表是否存在该告警
     * --如果存在，作为重复告警不予处理
     * --如果不存在，继续其他流程
     */
    private Alarm report( Alarm alarm){
        //判断实时表是否存在未消除
        Alarm sourceAlarm = alarmDao.getExistAlarm(alarm.getSliceKey(), alarm.getSignalId(), alarm.getSerial(), null, currentAlarmTable);
        if (null == sourceAlarm) {
            //判断历史表是否存在该告警
            sourceAlarm = alarmDao.getExistAlarm(alarm.getSliceKey(), alarm.getSignalId(), alarm.getSerial(), null, historyAlarmTable);
        }
        if (null != sourceAlarm) {
            System.out.printf("告警已存在: %s %n", sourceAlarm);
            return null;
        }
        //设置默认告警等级和颜色
        String alarmLevelName = EnumLevelName.getNameByLevel(alarm.getLevel());
        alarm.setTargetLevel(alarm.getLevel());
        alarm.setTargetLevelName(alarmLevelName);
        alarm.setColor(Contant.COLOR_BLACK);
        alarm.setTreport(new Date());
        return alarm;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/21 11:27
     * 功能描述:消除告警或者修改告警属性
     */
    private void  handleRecoverAndAuxilary() {
        if(recoverAndAuxilaryAlarmQueue.size() == 0){
            return ;
        }
        Alarm alarm = recoverAndAuxilaryAlarmQueue.poll();
        if(Contant.ZERO.equals(alarm.getFlag())) {
            boolean result = alarmDao.resolve(alarm.getSliceKey(), alarm.getSignalId(), alarm.getSerial(), Contant.RESOLVE, new Date(), currentAlarmTable);
            if (!result) {
                alarmDao.resolve(alarm.getSliceKey(), alarm.getSignalId(), alarm.getSerial(), Contant.RESOLVE, new Date(), historyAlarmTable);
            }
        }else{
            System.out.printf("修改告警属性: %s %n", alarm);
        }
        handleRecoverAndAuxilary();
    }
}
