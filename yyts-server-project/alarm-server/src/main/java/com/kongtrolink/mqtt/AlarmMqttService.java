package com.kongtrolink.mqtt;

import com.kongtrolink.enttiy.Auxilary;
import com.kongtrolink.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 09:09
 * @Description:告警产生或者消除mqtt消息处理
 */
@Service
public class AlarmMqttService {

    private static Map<String, Auxilary> uniqueServiceAuxiMap = new HashMap<>();
    //liuddtodo 系统启动后，填充附属属性map

    @Autowired
    AlarmService alarmService;

    /**
     * @auther: liudd
     * @date: 2019/9/24 11:20
     * 功能描述:告警产生上报
     */
    public void alarmReport(){

    }

    /**
     * @auther: liudd
     * @date: 2019/9/24 11:20
     * 功能描述:告警附属属性修改
     */
    public void updateaAuxilary(){

    }

}
