package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.core.entity.MongoTableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/3/28, 14:19.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class LogDao
{
    @Autowired
    MongoTemplate mongoTemplate;


    public void saveLog(Log log)
    {
        mongoTemplate.save(log, MongoTableName.LOG);
    }
}
