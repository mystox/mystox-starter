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

    /**
     * 获取指定类型与Id的告警信息
     * @param type 内部设备类型
     * @param id 告警点Id
     * @return 告警点信息
     */
    public Alarm getAlarmByTypeAndId(int type, String id) {
        Alarm result;

        Criteria criteria = Criteria
                .where("type").is(type)
                .and("alarmId").is(id);

        result = mongoTemplate.findOne(Query.query(criteria),
                Alarm.class, MongoDBTable.T_ALARM);

        return result;
    }

    /**
     * 更新或添加告警信息
     * @param alarm 告警信息
     * @return 更新或添加结果
     */
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
