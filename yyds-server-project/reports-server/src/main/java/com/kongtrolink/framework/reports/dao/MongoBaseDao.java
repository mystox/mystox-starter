package com.kongtrolink.framework.reports.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by mystoxlol on 2019/10/24, 19:06.
 * company: kongtrolink
 * description:
 * update record:
 */
public class MongoBaseDao {

    protected MongoTemplate mongoTemplate;
    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
