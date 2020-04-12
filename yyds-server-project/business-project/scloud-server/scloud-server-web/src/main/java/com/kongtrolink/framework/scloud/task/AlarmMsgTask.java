package com.kongtrolink.framework.scloud.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.mqtt.impl.MqttServiceImpl;
import com.kongtrolink.framework.scloud.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 16:30
 * @Description: 处理告警上报或消除任务
 */
public class AlarmMsgTask implements Runnable{
    private ShieldRuleService shieldRuleService;
    private WorkAlarmConfigService alarmConfigService;
    private AlarmService alarmService;
    private AlarmBusinessService businessService;
    private WorkService workService;
    private ConcurrentLinkedQueue<JSONObject> msgQueue = new ConcurrentLinkedQueue<>();
    private String cur_alarm_business = CollectionSuffix.CUR_ALARM_BUSINESS;
    private String his_alarm_business = CollectionSuffix.HIS_ALARM_BUSINESS;
    private Logger LOGGER = LoggerFactory.getLogger(AlarmMsgTask.class);

    public AlarmMsgTask(ShieldRuleService shieldRuleService, WorkAlarmConfigService alarmConfigService,
                        AlarmService alarmService, AlarmBusinessService businessService, WorkService workService, ConcurrentLinkedQueue<JSONObject> msgQueue) {
        this.shieldRuleService = shieldRuleService;
        this.alarmConfigService = alarmConfigService;
        this.alarmService = alarmService;
        this.businessService = businessService;
        this.workService = workService;
        this.msgQueue = msgQueue;
    }

    @Override
    public void run() {
        JSONObject jsonObject = msgQueue.poll();
        LOGGER.info(jsonObject.toString());
        String type = jsonObject.getString("type");
        String data = jsonObject.getString("data");
        if("1".equals(type)){
            alarmReport(data);
        }else{
            alarmResolve(data);
        }
    }

    private void alarmReport(String jsonStr){
        List<Alarm> alarmList = JSON.parseArray(jsonStr, Alarm.class);
        if(null == alarmList || alarmList.size() == 0){
            return ;
        }
        String enterpriseCode = alarmList.get(0).getEnterpriseCode();
        //填充你设备信息
        alarmService.initInfo(enterpriseCode, alarmList);
        List<AlarmBusiness> businessList = new ArrayList<>();
        for(Alarm alarm : alarmList){
            AlarmBusiness business = AlarmBusiness.createByAlarm(alarm);
            businessList.add(business);
        }

        //告警屏蔽功能
        shieldRuleService.matchRule(enterpriseCode, businessList);
        //匹配告警工单配置
        alarmConfigService.matchAutoConfig(enterpriseCode, businessList);
        businessService.add(enterpriseCode, cur_alarm_business, businessList);
    }

    private void alarmResolve(String jsonStr){
        List<Alarm> alarmList = JSON.parseArray(jsonStr, Alarm.class);
        if(null == alarmList || alarmList.size() == 0){
            return ;
        }
        String enterpriseCode = alarmList.get(0).getEnterpriseCode();
        List<AlarmBusiness> businessList = new ArrayList<>();
        for(Alarm alarm : alarmList){
            AlarmBusiness business = AlarmBusiness.createByAlarm(alarm);
            business.setState(BaseConstant.ALARM_STATE_RESOLVE);
            businessList.add(business);
        }
        workService.resolveAlarm(enterpriseCode, alarmList);
        for(AlarmBusiness alarmBusiness : businessList) {
            //修改实时告警表中告警状态
            boolean result = businessService.resolveByKey(enterpriseCode, cur_alarm_business, alarmBusiness);
            if(!result){
                businessService.resolveByKey(enterpriseCode, his_alarm_business, alarmBusiness);
            }
        }
    }
}