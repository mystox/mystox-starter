package com.kongtrolink.framework.mqtt.impl;

import com.kongtrolink.framework.mqtt.DeliverEntrance;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2019/10/15 17:03
 * @Description:
 */
@Service
public class DeliverEntranceImpl implements DeliverEntrance {
    /**
     * @auther: liudd
     * @date: 2019/10/15 17:04
     * 功能描述:告警投递入口
     * 为了加快告警处理入口保存告警对象，这里将告警信息放入队列后立即返回
     */
    @Override
    public void handleDelive(String alarmStr) {

        return ;
    }
}
