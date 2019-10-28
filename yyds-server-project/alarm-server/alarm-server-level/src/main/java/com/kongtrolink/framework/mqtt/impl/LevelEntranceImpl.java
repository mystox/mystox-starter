package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.mqtt.LevelEntrance;
import com.kongtrolink.framework.service.AlarmLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/12 14:00
 * @Description:
 */
@Service
public class LevelEntranceImpl implements LevelEntrance{

    @Autowired
    AlarmLevelService alarmLevelService;

    @Override
    public String handleLevel(String alarmListJsonStr) {
        List<Alarm> alarmList = JSON.parseArray(alarmListJsonStr, Alarm.class);
        for(Alarm alarm : alarmList) {
            AlarmLevel level = alarmLevelService.getLevelByAlarm(alarm);
            if (null != level) {
                alarm.setTargetLevel(level.getTargetLevel());
                alarm.setTargetLevelName(level.getTargetLevelName());
                alarm.setColor(level.getColor());
            }
        }
        return JSON.toJSONString(alarmList);
    }
}
