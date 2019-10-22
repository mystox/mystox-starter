package com.kongtrolink.mqtt.impl;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.dao.InformRuleDao;
import com.kongtrolink.framework.dao.InformRuleUserDao;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.InformRule;
import com.kongtrolink.framework.enttiy.InformRuleUser;
import com.kongtrolink.mqtt.DeliverEntrance;
import com.kongtrolink.service.AlarmDeliverService;
import com.kongtrolink.service.InformMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/21 18:56
 * @Description:
 */
@Service
public class DeliverEntranceImpl implements DeliverEntrance{

    @Autowired
    InformMsgService msgService;
    @Override
    public String handleDeliver(String alarmListJsonStr) {

        //liuddtodo 投递不影响告警其他流程，这里应该直接丢入新线程，然后返回。不同规则，使用的服务器可能不同
        List<Alarm> alarmList = JSON.parseArray(alarmListJsonStr, Alarm.class);
        msgService.handleInformAlarmList(alarmList, Contant.ONE);
        return null;
    }
}
