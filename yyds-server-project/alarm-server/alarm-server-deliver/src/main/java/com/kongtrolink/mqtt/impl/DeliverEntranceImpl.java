package com.kongtrolink.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.mqtt.DeliverEntrance;
import com.kongtrolink.service.CreateInformMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/21 18:56
 * @Description:
 * 1，获取对应的告警通知规则，并填充投递规则信息
 * 2，轮询获取对应的设备信息和区域信息
 * 3，根据投递规则中，标注的投递动作木块代码，调用投递动作发送推送
 */
@Service
public class DeliverEntranceImpl implements DeliverEntrance{

    @Autowired
    CreateInformMsgService msgService;
    @Override
    public String handleDeliver(String alarmListJsonStr) {
        List<Alarm> alarmList = JSON.parseArray(alarmListJsonStr, Alarm.class);
        msgService.handleInformAlarmList(alarmList, Contant.ONE);
        return alarmListJsonStr;
    }
}
