package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.mqtt.LevelEntrance;
import com.kongtrolink.framework.service.AlarmLevelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(LevelEntranceImpl.class);

    @Override
    public String handleLevel(String alarmListJsonStr) {
        logger.debug("receive ：{}", alarmListJsonStr);
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

    /**
     * @auther: liudd
     * @date: 2019/11/6 16:16
     * 功能描述:web端修改企业告警等级
     */
    @Override
    public void updateEnterpriseLevelMap(String jsonStr) {
        alarmLevelService.updateEnterpriseLevelMap(jsonStr);
    }

    /**
     * @auther: liudd
     * @date: 2019/12/3 15:18
     * 功能描述:web端修改告警等级后，同步修改告警等级模块内存中告警等级
     */
    @Override
    public void updateAlarmLevelMap(String jsonStr) {
        alarmLevelService.updateAlarmLevelMap(jsonStr);
    }
}