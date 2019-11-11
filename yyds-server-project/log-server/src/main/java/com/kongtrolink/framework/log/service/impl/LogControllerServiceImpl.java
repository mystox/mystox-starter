package com.kongtrolink.framework.log.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MqttLog;
import com.kongtrolink.framework.log.dao.MqttLogDao;
import com.kongtrolink.framework.log.service.LogControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mystoxlol on 2019/11/11, 11:22.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class LogControllerServiceImpl implements LogControllerService {

    @Autowired
    MqttLogDao mqttLogDao;
    @Override
    public JSONObject getMqttLogList(JSONObject query) {
        List<MqttLog> mqttLog = mqttLogDao.findMqttLog(query);
        long mqttLogCount = mqttLogDao.findMqttLogCount(query);
        JSONObject result = new JSONObject();
        result.put("total", mqttLogCount);
        result.put("list", mqttLog);
        return result;
    }
}
