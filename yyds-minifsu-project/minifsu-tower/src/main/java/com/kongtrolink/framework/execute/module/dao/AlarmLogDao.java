package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.entity.MongoDBTable;
import com.kongtrolink.framework.execute.module.model.RedisAlarm;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * @author fengw
 * 告警记录数据库操作
 * 新建文件 2019-4-22 14:41:57
 */
@Component
public class AlarmLogDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 将告警信息存入数据库
     * @param tAlarm 告警信息
     * @return 操作结果
     */
    public boolean upsert(RedisAlarm tAlarm) {
        boolean result;

        Criteria criteria = Criteria
                .where("fsuId").is(tAlarm.getFsuId())
                .and("serialNo").is(tAlarm.getSerialNo());

        Update update = new Update();
        update.set("deviceId", tAlarm.getDeviceId());
        update.set("id", tAlarm.getId());
        update.set("startTime", tAlarm.getStartTime());
        update.set("endTime", tAlarm.getEndTime());
        update.set("alarmLevel", tAlarm.getAlarmLevel());
        update.set("alarmFlag", tAlarm.getAlarmFlag());
        update.set("alarmDesc", tAlarm.getAlarmDesc());

        update.set("value", tAlarm.getValue());
        update.set("highFrequency", tAlarm.isHighFrequency());
        update.set("startReported", tAlarm.isStartReported());
        update.set("endReported", tAlarm.isEndReported());
        update.set("lastReportedTime", tAlarm.getLastReportTime());
        update.set("reportCount", tAlarm.getReportCount());

        WriteResult writeResult = mongoTemplate.upsert(Query.query(criteria),
                update, MongoDBTable.T_ALARM_LOG);

        result = writeResult.getN() > 0;

        return result;
    }
}
