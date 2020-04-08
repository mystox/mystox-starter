package com.kongtrolink.framework.scloud.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.mqtt.impl.MqttServiceImpl;
import com.kongtrolink.framework.scloud.service.AlarmService;
import com.kongtrolink.framework.scloud.service.ShieldRuleService;
import com.kongtrolink.framework.scloud.service.WorkAlarmConfigService;
import com.kongtrolink.framework.scloud.service.WorkService;
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
    private WorkService workService;
    private ConcurrentLinkedQueue<JSONObject> msgQueue = new ConcurrentLinkedQueue<>();

    public AlarmMsgTask(ShieldRuleService shieldRuleService, WorkAlarmConfigService alarmConfigService,
                        AlarmService alarmService, WorkService workService, ConcurrentLinkedQueue<JSONObject> msgQueue) {
        this.shieldRuleService = shieldRuleService;
        this.alarmConfigService = alarmConfigService;
        this.alarmService = alarmService;
        this.workService = workService;
        this.msgQueue = msgQueue;
    }
    private Logger LOGGER = LoggerFactory.getLogger(AlarmMsgTask.class);
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
        shieldRuleService.matchRule(enterpriseCode, alarmList);
        //匹配告警工单配置
        alarmConfigService.matchAutoConfig(alarmList);
    }

    private void alarmResolve(String jsonStr){
        List<Alarm> alarmList = JSON.parseArray(jsonStr, Alarm.class);
        if(null == alarmList || alarmList.size() == 0){
            return ;
        }
        Alarm alarm = alarmList.get(0);
        workService.resolveAlarm(alarm.getEnterpriseCode(), alarmList);
    }
}
