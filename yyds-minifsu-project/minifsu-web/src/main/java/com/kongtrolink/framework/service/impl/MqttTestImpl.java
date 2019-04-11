package com.kongtrolink.framework.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.mqtt.base.MqttRequestHelper;
import com.kongtrolink.framework.service.MqttTestInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by mystoxlol on 2019/2/27, 17:32.
 * company: kongtrolink
 * description:
 * update record:
 */
//@Service
public class MqttTestImpl implements MqttTestInterface
{
    @Value("${mqtt.sub.ywcl.topicId}")
    private String omcTopic;
    @Autowired
    MqttRequestHelper mqttRequestHelper;
    @Override
    public JSONObject sendMsg(String requestBody)
    {
        if (requestBody == null) return null;

        JSONObject result = mqttRequestHelper.sendContext(omcTopic, requestBody);
        return result;
    }
}
