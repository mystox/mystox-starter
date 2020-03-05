package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.ShieldAlarmDao;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.ShieldAlarm;
import com.kongtrolink.framework.scloud.query.ShieldAlarmQuery;
import com.kongtrolink.framework.scloud.service.AlarmService;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.ShieldAlarmService;
import com.kongtrolink.framework.scloud.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 09:21
 * @Description:
 */
@Service
public class ShieldAlarmServiceImpl implements ShieldAlarmService{

    @Autowired
    ShieldAlarmDao shieldAlarmDao;
    @Autowired
    AlarmService alarmService;

    @Override
    public boolean add(String uniqueCode, ShieldAlarm shieldAlarm) {
        return shieldAlarmDao.add(uniqueCode, shieldAlarm);
    }

    @Override
    public List<ShieldAlarm> list(String uniqueCode, ShieldAlarmQuery shieldAlarmQuery) {
        return shieldAlarmDao.list(uniqueCode, shieldAlarmQuery);
    }

    @Override
    public int count(String uniqueCode, ShieldAlarmQuery shieldAlarmQuery) {
        return shieldAlarmDao.count(uniqueCode, shieldAlarmQuery);
    }

    /**
     * @auther: liudd
     * @date: 2020/3/5 9:57
     * 功能描述:填充信息
     */
    @Override
    public void initInfo(String uniqueCode, List<ShieldAlarm> shieldAlarmList) {
        if(null == shieldAlarmList){
            return;
        }
        List<Alarm> alarmList = new ArrayList<>();
        Map<String, Alarm> alarmIdAlarmMap = new HashMap<>();
        for(ShieldAlarm shieldAlarm : shieldAlarmList){
            Alarm alarm = new Alarm();
            alarm.setId(shieldAlarm.getAlarmId());
            alarm.setTreport(shieldAlarm.getTreport());
            alarm.setTargetLevelName(shieldAlarm.getAlarmLevel());
            alarm.setSignalId(shieldAlarm.getSignalId());
            alarm.setDeviceId(shieldAlarm.getDeviceId());
            alarmIdAlarmMap.put(alarm.getId(), alarm);
        }
        alarmService.initInfo(uniqueCode, alarmList);
        for(ShieldAlarm shieldAlarm : shieldAlarmList){
            Alarm alarm = alarmIdAlarmMap.get(shieldAlarm.getAlarmId());
            shieldAlarm.initAlarmInfo(alarm);
        }
    }
}
