package com.kongtrolink.framework.scloud.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.mqtt.MqttService;
import com.kongtrolink.framework.scloud.service.*;
import com.kongtrolink.framework.scloud.task.AlarmMsgTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private ConcurrentLinkedQueue<AlarmBusiness> msgQueue = new ConcurrentLinkedQueue<>();

    /**
     * @auther: liudd
     * @date: 2020/3/20 15:11
     * 功能描述:山东国动告警上报切入。这里可能需要获取设备信息，站点信息的数据
     */
    @Override
    public String sdgdScloudAlarmReport(String jsonStr) {
        LOGGER.info("sdgdScloudAlarmReport--receive:{}", jsonStr);
        List<Alarm> alarmList = JSONObject.parseArray(jsonStr, Alarm.class);
        List<AlarmBusiness> businessList = new ArrayList<>();
        for(Alarm alarm : alarmList){
            AlarmBusiness alarmBusiness = AlarmBusiness.createByAlarm(alarm);
            businessList.add(alarmBusiness);
        }
        msgQueue.addAll(businessList);
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
        LOGGER.debug("sdgdScloudAlarmResolve--receive:{}", jsonStr);
        List<Alarm> alarmList = JSONObject.parseArray(jsonStr, Alarm.class);
        List<AlarmBusiness> businessList = new ArrayList<>();
        for(Alarm alarm : alarmList){
            AlarmBusiness alarmBusiness = AlarmBusiness.createByAlarm(alarm);
            businessList.add(alarmBusiness);
        }
        msgQueue.addAll(businessList);
        scloudWebExecutor.execute(new AlarmMsgTask(shieldRuleService, alarmConfigService, alarmService, businessService, workService, msgQueue));
//        List<Alarm> alarmList = JSON.parseArray(jsonStr, Alarm.class);
//        if(null == alarmList || alarmList.size() == 0){
//            return jsonStr;
//        }
//        Alarm alarm = alarmList.get(0);
//        workService.resolveAlarm(alarm.getEnterpriseCode(), alarmList);
        return jsonStr;
    }

    @Override
    public String sdgdScloudAlarmHistory(String jsonStr) {
        LOGGER.debug("sdgdScloudAlarmHistory--receive:{}", jsonStr);
        List<String> keyList = JSONObject.parseArray(jsonStr, String.class);
        Map<String, List<String>> enterpriseCodeKeyListMap = new HashMap<>();
        for(String key : keyList){
            String enterpriseCode = key.substring(0, key.indexOf(BaseConstant.UNDERLINE));
            List<String> enterpriseCodeKeyList = enterpriseCodeKeyListMap.get(enterpriseCode);
            if(null == enterpriseCodeKeyList){
                enterpriseCodeKeyList = new ArrayList<>();
            }
            enterpriseCodeKeyList.add(key);
        }
        //从实时表获取告警列表，然后删除
        for(String enterpriseCode : enterpriseCodeKeyListMap.keySet()){
            List<String> keys = enterpriseCodeKeyListMap.get(enterpriseCode);
            List<AlarmBusiness> businessList = businessService.listByKeyList(enterpriseCode, CollectionSuffix.CUR_ALARM_BUSINESS, keys);
            boolean addResult = businessService.add(enterpriseCode, CollectionSuffix.HIS_ALARM_BUSINESS, businessList);
            if(addResult) {
                businessService.deleteByKeyList(enterpriseCode, CollectionSuffix.CUR_ALARM_BUSINESS, keys);
            }
        }

        return jsonStr;
    }
}
