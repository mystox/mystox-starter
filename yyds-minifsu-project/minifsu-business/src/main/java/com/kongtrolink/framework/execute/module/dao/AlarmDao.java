package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.MongoTableName;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @Auther: liudd
 * @Date: 2019/4/1 20:28
 * @Description:
 */
@Repository
public class AlarmDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String tableName = MongoTableName.ALARM;

    /**
     * @auther: liudd
     * @date: 2019/4/1 20:33
     * 功能描述:根据告警点id修改或者添加告警
     */
    public boolean AddOrUpdateByAlarmId(Alarm alarm){
        Criteria criteria = Criteria.where("alarmId").is(alarm.getAlarmId());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("link", alarm.getLink());
        update.set("updateTime", alarm.getUpdateTime());
        update.set("value", alarm.getValue());
        WriteResult upsert = mongoTemplate.upsert(query, update, tableName);
        return upsert.getN()>0 ? true : false;
    }

}
