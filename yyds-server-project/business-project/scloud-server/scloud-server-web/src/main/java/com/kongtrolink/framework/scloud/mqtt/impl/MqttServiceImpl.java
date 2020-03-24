package com.kongtrolink.framework.scloud.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.mqtt.MqttService;
import com.kongtrolink.framework.scloud.service.ShieldRuleService;
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
    private Logger LOGGER = LoggerFactory.getLogger(MqttServiceImpl.class);

    /**
     * @auther: liudd
     * @date: 2020/3/20 15:11
     * 功能描述:山东国动告警上报切入
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
        //告警过滤功能
        shieldRuleService.matchRule(enterpriseCode, alarmList);
        return JSON.toJSONString(alarmList);
    }
}
