package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.entity.MongoDBTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class LogDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public void save(Log log) {
        mongoTemplate.save(log, MongoDBTable.T_LOG);
    }
}
