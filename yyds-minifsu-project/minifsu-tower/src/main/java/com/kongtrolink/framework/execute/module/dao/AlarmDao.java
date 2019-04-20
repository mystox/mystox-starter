package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.entity.MongoDBTable;
import com.kongtrolink.framework.execute.module.model.Alarm;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * @author fengw
 * 告警点数据库操作
 * 新建文件 2019-4-19 08:34:08
 */
@Component
public class AlarmDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public boolean upsert(Alarm alarm) {
        boolean result;

        Criteria criteria = Criteria
                .where("type").is(alarm.getType())
                .and("alarmId").is(alarm.getAlarmId());

        Update update = new Update();
        update.set("cntbId", alarm.getCntbId());
        update.set("level", alarm.getLevel());
        update.set("desc", alarm.getDesc());

        WriteResult writeResult = mongoTemplate.upsert(Query.query(criteria),
                update, MongoDBTable.T_ALARM);

        result = writeResult.getN() > 0;

        return result;
    }
}
