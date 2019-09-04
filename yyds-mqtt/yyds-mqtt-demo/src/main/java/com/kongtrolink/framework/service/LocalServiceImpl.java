package com.kongtrolink.framework.service;

import com.kongtrolink.framework.api.LocalService;
import com.kongtrolink.framework.entity.MqttMsg;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/8/15, 13:33.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class LocalServiceImpl implements LocalService {
    @Override
    public String hello(MqttMsg mqttMsg) {
        System.out.println("这是local收到的消息：============"+mqttMsg.toString());
        return "hello_ack";
}
}
