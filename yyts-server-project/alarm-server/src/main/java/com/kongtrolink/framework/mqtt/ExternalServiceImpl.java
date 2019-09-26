package com.kongtrolink.framework.mqtt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.EnumLevelName;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.service.AlarmLevelService;
import com.kongtrolink.framework.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/9/26 09:32
 * @Description:
 */
@Service
public class ExternalServiceImpl implements ExternalService {

    @Autowired
    AlarmService alarmService;
    @Autowired
    AlarmLevelService alarmLevelService;
    private String currentAlarmTable = MongTable.ALARM;
    private String historyAlarmTable = MongTable.ALARM_HISTORY;

    @Override
    public void alarmReport(String payload) {
        Alarm alarm = JSONObject.parseObject(payload, Alarm.class);
        if(null == alarm){
            System.out.println("产生告警为空: " + payload);
        }
        alarm.setState(Contant.PENDING);
        //实时表是否存在未消除告警
        AlarmQuery alarmQuery = AlarmQuery.alarm2AlarmQuery(alarm);
        Alarm sourceAlarm = alarmService.getOne(alarmQuery, currentAlarmTable);
        if(null == sourceAlarm){
            sourceAlarm = alarmService.getOne(alarmQuery, historyAlarmTable);
        }
        if(null != sourceAlarm){
            System.out.println("重复告警:" + sourceAlarm);
            return ;
        }
        AlarmLevelQuery alarmLevelQuery = AlarmLevelQuery.alarm2AlarmLevelQuery(alarm);
        AlarmLevel alarmLevel = alarmLevelService.getAlarmLevel(alarmLevelQuery);
        if(null != alarmLevel){
            alarm.setTargetLevel(alarmLevel.getTargetLevel());
            alarm.setTargetLevelName(alarmLevel.getTargetLevelName());
            alarm.setColor(alarm.getColor());
        }else{
            alarm.setTargetLevel(alarm.getLevel());
            alarm.setTargetLevelName(EnumLevelName.getNameByLevel(alarm.getLevel()));
            alarm.setColor(Contant.COLOR_BLACK);
        }
        alarm.settReport(new Date());
        alarmService.save(alarm, currentAlarmTable);
        return ;
    }

    @Override
    public void alarmRecover(String payload) {
        Alarm alarm = JSONObject.parseObject(payload, Alarm.class);
        if(null == alarm){
            System.out.println("消除告警为空: " + payload);
        }
        //查看是否存在未消除告警
        alarm.setState(Contant.PENDING);
        AlarmQuery alarmQuery = AlarmQuery.alarm2AlarmQuery(alarm);
        alarm.setState(Contant.RESOLVE);
        alarm.settRecover(new Date());
        Alarm sourceAlarm = alarmService.getOne(alarmQuery, currentAlarmTable);
        if(null != sourceAlarm){
            alarmService.resolve(alarm, currentAlarmTable);
            return ;
        }
        sourceAlarm = alarmService.getOne(alarmQuery, currentAlarmTable);
        if(null != sourceAlarm){
            alarmService.resolve(alarm, historyAlarmTable);
            return ;
        }
        System.out.println("消除告警不存在：" + alarm.toString());
    }

    @Override
    public void updateaAuxilary(String payload) {
        AlarmQuery alarmQuery = JSONObject.parseObject(payload, AlarmQuery.class);

    }
}
