package com.kongtrolink.framework.log.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MqttLog;
import com.kongtrolink.framework.log.entity.MongoDocName;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by mystoxlol on 2019/11/11, 11:24.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class MqttLogDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public void save(MqttLog mqttLog) {
        mongoTemplate.save(mqttLog, MongoDocName.MQTT_LOG);
    }

    public List<MqttLog> findMqttLog(JSONObject query) {
        int count = query.get("count") == null ? 30 : (int) query.get("count");
        int page = query.get("page") == null ? 1 : (int) query.get("page");
        Criteria criteria = new Criteria();
        Long startTime = query.getLong("startTime");
        if (startTime != null) {
            Long endTime = query.getLong("endTime");
            if (endTime != null) {
                criteria.and("recordTime").gte(new Date(startTime)).lte(new Date(endTime));
            } else {
                criteria.and("recordTime").gte(new Date(startTime));
            }
        }
        String serverCode = query.getString("serverCode");
        if (StringUtils.isNotBlank(serverCode)) {
            criteria = criteria.and("serverCode").is(serverCode);
        }

        String sourceServerCode = query.getString("sourceServerCode");
        if (StringUtils.isNotBlank(sourceServerCode)) {
            criteria = criteria.and("sourceServerCode").is(sourceServerCode);
        }
        String operaCode = query.getString("operaCode");
        if (StringUtils.isNotBlank(operaCode)) {
            criteria = criteria.and("operaCode").is(operaCode);
        }
        Integer stateCode = query.getInteger("stateCode");
        if (serverCode!=null) {
            criteria = criteria.and("stateCode").is(stateCode);
        }
        return mongoTemplate.find(Query.query(criteria).skip((page - 1) * count).limit(count),
                MqttLog.class, MongoDocName.MQTT_LOG);


    }


    public long findMqttLogCount(JSONObject query) {
        int count = query.get("count") == null ? 30 : (int) query.get("count");
        int page = query.get("page") == null ? 1 : (int) query.get("page");
        Criteria criteria = new Criteria();

        Long startTime = query.getLong("startTime");
        if (startTime != null) {
            Long endTime = query.getLong("endTime");
            if (endTime != null) {
                criteria.and("recordTime").gte(new Date(startTime)).lte(new Date(endTime));
            } else {
                criteria.and("recordTime").gte(new Date(startTime));
            }
        }
        String serverCode = query.getString("serverCode");
        if (StringUtils.isNotBlank(serverCode)) {
            criteria = criteria.and("serverCode").is(serverCode);
        }

        String sourceServerCode = query.getString("sourceServerCode");
        if (StringUtils.isNotBlank(sourceServerCode)) {
            criteria = criteria.and("sourceServerCode").is(sourceServerCode);
        }
        String operaCode = query.getString("operaCode");
        if (StringUtils.isNotBlank(operaCode)) {
            criteria = criteria.and("operaCode").is(operaCode);
        }
        Integer stateCode = query.getInteger("stateCode");
        if (serverCode!=null) {
            criteria = criteria.and("stateCode").is(stateCode);
        }
        return mongoTemplate.count(Query.query(criteria).skip((page - 1) * count).limit(count),
                MongoDocName.MQTT_LOG);


    }
}
