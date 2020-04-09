package com.kongtrolink.framework.scloud.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.mqtt.MqttService;
import com.kongtrolink.framework.scloud.service.*;
import com.kongtrolink.framework.scloud.task.AlarmMsgTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;

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
    @Autowired
    ThreadPoolTaskExecutor scloudWebExecutor;
    @Autowired
    AlarmBusinessService businessService;

    private Logger LOGGER = LoggerFactory.getLogger(MqttServiceImpl.class);
    private ConcurrentLinkedQueue<JSONObject> msgQueue = new ConcurrentLinkedQueue<>();

    /**
     * @auther: liudd
     * @date: 2020/3/20 15:11
     * 功能描述:山东国动告警上报切入。这里可能需要获取设备信息，站点信息的数据
     */
    @Override
    public String sdgdScloudAlarmReport(String jsonStr) {
        LOGGER.debug("receive:{}", jsonStr);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "1");
        jsonObject.put("data", jsonObject);
        msgQueue.add(jsonObject);
        scloudWebExecutor.execute(new AlarmMsgTask(shieldRuleService, alarmConfigService, alarmService, businessService, workService, msgQueue));
//        List<Alarm> alarmList = JSON.parseArray(jsonStr, Alarm.class);
//        if(null == alarmList || alarmList.size() == 0){
//            return jsonStr;
//        }
//        Alarm alarm = alarmList.get(0);
//        String enterpriseCode = alarm.getEnterpriseCode();
//        //填充你设备信息
//        alarmService.initInfo(enterpriseCode, alarmList);
//
//        //告警屏蔽功能
//        shieldRuleService.matchRule(enterpriseCode, alarmList);
//        //匹配告警工单配置
//        alarmConfigService.matchAutoConfig(alarmList);
        return jsonStr;
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 15:20
     * 功能描述:山东国动告警消除切入
     */
    @Override
    public String sdgdScloudAlarmResolve(String jsonStr) {
        LOGGER.debug("receive:{}", jsonStr);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "0");
        jsonObject.put("data", jsonObject);
        msgQueue.add(jsonObject);
        scloudWebExecutor.execute(new AlarmMsgTask(shieldRuleService, alarmConfigService, alarmService, businessService, workService, msgQueue));
//        List<Alarm> alarmList = JSON.parseArray(jsonStr, Alarm.class);
//        if(null == alarmList || alarmList.size() == 0){
//            return jsonStr;
//        }
//        Alarm alarm = alarmList.get(0);
//        workService.resolveAlarm(alarm.getEnterpriseCode(), alarmList);
        return jsonStr;
    }
}
