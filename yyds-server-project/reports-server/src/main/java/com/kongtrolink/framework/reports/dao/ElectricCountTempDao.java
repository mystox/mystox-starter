package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.SystemReports.ElectricCountTemp;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/6 13:49
 * \* Description:
 * \
 */
@Service
public class ElectricCountTempDao extends MongoBaseDao {

    public void save(List<ElectricCountTemp> electricCountTempList, String taskId) {
        mongoTemplate.insert(electricCountTempList, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_ELECTRIC_COUNT);
    }

    public List<JSONObject> getElectricCountByCondition(String id, List<JSONObject> jsonObjects, String period, JSONObject timePeriod) {
        return null;
    }

    public void updateDelete(int year, int month, String reportTaskId) {
        Query query = Query.query(Criteria.where("year").is(year).and("month").is(month));
        Update update = Update.update("deleteFlag", true);
        mongoTemplate.updateMulti(query, update, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_ELECTRIC_COUNT + reportTaskId);

    }
}