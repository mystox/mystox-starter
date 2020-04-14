package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.alarmHistory.AlarmHistoryTemp;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/4/9 10:48
 * \* Description:
 * \
 */
@Service
public class AlarmHistoryTempDao extends MongoBaseDao{

    public void save(List<AlarmHistoryTemp> alarmHistoryTemps, String taskId) {
        mongoTemplate.insert(alarmHistoryTemps, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_ALARM_HISTORY + taskId);
    }

    public List<AlarmHistoryTemp> findAlarmDetailsList(String taskId, JSONObject condition) {
        String statisticPeriod = condition.getString("statisticPeriod");
        Criteria criteria = Criteria.where("type").is(statisticPeriod);

        return mongoTemplate.find(Query.query(criteria), AlarmHistoryTemp.class, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_ALARM_HISTORY + taskId);

    }
}