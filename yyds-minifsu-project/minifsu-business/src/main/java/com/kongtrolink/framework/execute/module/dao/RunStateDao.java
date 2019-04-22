package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.core.entity.MongoTableName;
import com.kongtrolink.framework.execute.module.model.RunState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/4/22, 10:54.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class RunStateDao {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public RunStateDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public void saveRunState(RunState runState) {
        mongoTemplate.insert(runState, MongoTableName.TERMINAL_RUN_STATE);

    }






}
