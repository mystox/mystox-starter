package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.EnumLevelName;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.mqtt.AlarmEntrance;
import com.kongtrolink.framework.mqtt.config.MqttConfig;
import com.kongtrolink.framework.query.AlarmQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * @Auther: liudd
 * @Date: 2019/10/12 15:11
 * @Description:
 */
@Service
public class AlarmEntranceImpl implements AlarmEntrance {

    private String currentAlarmTable = MongTable.ALARM;
    private String historyAlarmTable = MongTable.ALARM_HISTORY;
    Logger logger = LoggerFactory.getLogger(AlarmEntranceImpl.class);

    @Autowired
    AlarmDao alarmDao;
    /**
     * @auther: liudd
     * @date: 2019/10/14 8:50
     * 功能描述:告警上报，如果已存在未消除告警，则跳过不处理
     * 1,实时告警表
     * 2，历史告警表
     */
    @Override
    public void alarmReport(String payload) {
        Alarm alarm = JSONObject.parseObject(payload, Alarm.class);
        if(null == alarm){
            logger.info("产生告警为空: " + payload);
        }
        //初始化片键
        alarm.initSliceKey();
        //判断实时表是否存在未消除
        AlarmQuery alarmQuery = new AlarmQuery();
        alarmQuery.setSliceKey(alarm.getSliceKey());
        alarmQuery.setDeviceId(alarm.getDeviceId());
        alarmQuery.setSignalId(alarm.getSignalId());
        alarmQuery.setState(Contant.PENDING);
        Alarm sourceAlarm = alarmDao.getOne(alarmQuery, currentAlarmTable);
        if(null == sourceAlarm){
            //判断历史表是否存在该告警
            sourceAlarm = alarmDao.getOne(alarmQuery, historyAlarmTable);
        }
        if(null != sourceAlarm){
            logger.info("重复告警:" + sourceAlarm);
            return ;
        }
        //设置默认告警等级和颜色
        String alarmLevelName = EnumLevelName.getNameByLevel(alarm.getLevel());
        alarm.setTargetLevel(alarm.getLevel());
        alarm.setTargetLevelName(alarmLevelName);
        alarm.setColor(Contant.COLOR_BLACK);
        //liuddtodo 按照配置文件，调用各个服务


        //保存实时
        alarm.setTreport(new Date());
        alarmDao.save(alarm, currentAlarmTable);
        return ;
    }

    @Override
    public void alarmRecover(String payload) {
        Alarm alarm = JSONObject.parseObject(payload, Alarm.class);
        if(null == alarm){
            logger.info("消除告警为空: " + payload);
        }
        //查看是否存在未消除告警
        alarm.initSliceKey();
        AlarmQuery alarmQuery = new AlarmQuery();
        alarmQuery.setSliceKey(alarm.getSliceKey());
        alarmQuery.setDeviceId(alarm.getDeviceId());
        alarmQuery.setSignalId(alarm.getSignalId());
        alarmQuery.setState(Contant.PENDING);
        alarmQuery.setSerial(alarm.getSerial());

        boolean resolve = alarmDao.resolve(alarmQuery, currentAlarmTable, new Date());
        if(resolve){
            return ;
        }
        alarmDao.resolve(alarmQuery, historyAlarmTable, new Date());
        logger.info("消除告警不存在：" + alarm.toString());
        return ;
    }

    @Override
    public void updateaAuxilary(String payload) {
        Alarm alarm = JSONObject.parseObject(payload, Alarm.class);
        //初始化查询条件
        alarm.initSliceKey();
        AlarmQuery alarmQuery = new AlarmQuery();
        alarmQuery.setSliceKey(alarm.getSliceKey());
        alarmQuery.setDeviceId(alarm.getDeviceId());
        alarmQuery.setSignalId(alarm.getSignalId());

    }
}
