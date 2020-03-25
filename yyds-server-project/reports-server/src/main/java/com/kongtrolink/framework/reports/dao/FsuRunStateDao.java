package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.StatisticLevel;
import com.kongtrolink.framework.reports.entity.fsu.FsuRunStateTemp;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/18 10:17
 * \* Description:
 * \
 */
@Service
public class FsuRunStateDao extends MongoBaseDao {

    public void save(List<FsuRunStateTemp> fsuRunStateTemps, String taskId) {
        mongoTemplate.insert(fsuRunStateTemps, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_FSU_RUNNING_STATE+taskId);
    }

    public void updateDelete(int year, int month, String taskId) {
        Query query = Query.query(Criteria.where("year").is(year).and("month").is(month));
        Update update = Update.update("deleteFlag", true);
        mongoTemplate.updateMulti(query, update, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_FSU_RUNNING_STATE + taskId);
    }


    public List<JSONObject> getFsuRunStateData(String taskId, JSONObject condition) {
        String monthStr = condition.getString("month");
        String[] split = monthStr.split("-");
        Criteria criteria = Criteria.where("deleteFlag").is(false);

        String stationType = condition.getString("stationType");
        if (!"全部".equals(stationType)) criteria.and("stationType").is(stationType);
        String fsuManufactory = condition.getString("fsuManufactory");
        if (!"全部".equals(fsuManufactory)) criteria.and("fsuManufactory").is(fsuManufactory);
        JSONArray stationList = condition.getJSONArray("stationList");
        if (!CollectionUtils.isEmpty(stationList)) {
            List<String> siteIdList = stationList.toJavaList(String.class);
            criteria.and("stationId").in(siteIdList);
        }
        criteria.and("year").is(Integer.valueOf(split[0])).and("month").is(Integer.valueOf(split[1]));
        String statisticLevel = condition.getString("statisticLevel");

        Fields fields = Fields.fields();
        if (StatisticLevel.province.equals(statisticLevel)) {
            fields = fields.and(Fields.fields("province"));
        }
        if (StatisticLevel.municipality.equals(statisticLevel)) {
            fields = fields.and(Fields.fields("municipality"));
        }
        if (StatisticLevel.county.equals(statisticLevel)) {
            fields = fields.and(Fields.fields("county"));
        }
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group(fields)
                        .sum("fsuMaintenanceCount").as("fsuMaintenanceCount")
                        .sum("onlineCount").as("onlineCount")
                        .sum("offlineCount").as("offlineCount"));
        AggregationResults<JSONObject> results = mongoTemplate.aggregate(aggregation, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_ALARM_COUNT + taskId, JSONObject.class);
        List<JSONObject> mappedResults = results.getMappedResults();
        for (JSONObject fsuRunning : mappedResults) {
            Integer fsuMaintenanceCount = fsuRunning.getInteger("fsuMaintenanceCount");
            Integer onlineCount = fsuRunning.getInteger("onlineCount");
            Integer offlineCount = fsuRunning.getInteger("offlineCount");
            fsuRunning.put("onlineRate", String.format("%d%s", 100 * onlineCount / fsuMaintenanceCount, "%"));
            fsuRunning.put("offlineRate", String.format("%d%s", 100 * offlineCount / fsuMaintenanceCount, "%"));
        }
        return mappedResults;

    }
}