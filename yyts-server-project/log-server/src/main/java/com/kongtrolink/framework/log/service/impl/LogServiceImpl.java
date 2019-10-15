package com.kongtrolink.framework.log.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MqttLog;
import com.kongtrolink.framework.log.api.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/14, 16:21.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void mqLog(String moduleMsgStr) {
        MqttLog mqttLog = JSONObject.parseObject(moduleMsgStr, MqttLog.class);
        mongoTemplate.save(mqttLog);
    }
}
