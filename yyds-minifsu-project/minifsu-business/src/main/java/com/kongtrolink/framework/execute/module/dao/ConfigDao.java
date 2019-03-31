package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.execute.module.model.AlarmSignalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mystoxlol on 2019/3/31, 20:20.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class ConfigDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<AlarmSignalConfig> findAlarmSignalConfigByDevId(String devId) {

        return mongoTemplate.find(Query.query(Criteria.where("devId").in(devId)), AlarmSignalConfig.class);
    }
}
