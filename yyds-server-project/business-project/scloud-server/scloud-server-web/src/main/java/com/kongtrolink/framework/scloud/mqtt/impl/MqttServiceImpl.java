package com.kongtrolink.framework.scloud.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.mqtt.MqttService;
import com.kongtrolink.framework.scloud.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/20 13:22
 * @Description:
 */
@Service
public class MqttServiceImpl implements MqttService {

    @Autowired
    ShieldRuleService shieldRuleService;
    @Autowired
    WorkAlarmConfigService alarmConfigService;
    @Autowired
    AlarmService alarmService;
    @Autowired
    WorkService workService;

    private Logger LOGGER = LoggerFactory.getLogger(MqttServiceImpl.class);

    /**
     * @auther: liudd
     * @date: 2020/3/20 15:11
     * 功能描述:山东国动告警上报切入。这里可能需要获取设备信息，站点信息的数据
     */
    @Override
    public String sdgdScloudAlarmReport(String jsonStr) {
        LOGGER.debug("receive:{}", jsonStr);
        List<Alarm> alarmList = JSON.parseArray(jsonStr, Alarm.class);
        if(null == alarmList || alarmList.size() == 0){
            return jsonStr;
        }
        Alarm alarm = alarmList.get(0);
        String enterpriseCode = alarm.getEnterpriseCode();
        //填充你设备信息
        alarmService.initInfo(enterpriseCode, alarmList);

        //告警过滤功能
        shieldRuleService.matchRule(enterpriseCode, alarmList);
        //匹配告警工单配置
        alarmConfigService.matchAutoConfig(alarmList);
        return JSON.toJSONString(alarmList);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 15:20
     * 功能描述:山东国动告警消除切入
     */
    @Override
    public String sdgdScloudAlarmResolve(String jsonStr) {
        LOGGER.debug("receive:{}", jsonStr);
        List<Alarm> alarmList = JSON.parseArray(jsonStr, Alarm.class);
        if(null == alarmList || alarmList.size() == 0){
            return jsonStr;
        }
        Alarm alarm = alarmList.get(0);
        workService.resolveAlarm(alarm.getEnterpriseCode(), alarmList);
        return jsonStr;
    }
}
