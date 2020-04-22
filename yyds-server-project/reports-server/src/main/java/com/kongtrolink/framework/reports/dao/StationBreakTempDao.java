package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.StatisticLevel;
import com.kongtrolink.framework.reports.entity.SystemReports.StationBreakTemp;
import com.kongtrolink.framework.reports.entity.TimePeriod;
import com.kongtrolink.framework.reports.entity.query.FsuOperationState;
import com.kongtrolink.framework.reports.utils.CommonCheck;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/6 9:10
 * \* Description:
 * \
 */
@Service
public class StationBreakTempDao extends MongoBaseDao {

    public void save(List<StationBreakTemp> stationBreakTempList, String taskId) {
        mongoTemplate.save(stationBreakTempList, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_STATION_BREAK + taskId);
    }


    public List<JSONObject> getStationBreakByCondition(String taskId, JSONObject condition, TimePeriod timePeriod) {
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
        Date startTime = timePeriod.getStartTime();
        Date endTime = timePeriod.getEndTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int year = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);
        calendar.setTime(endTime);
        int endMonth = calendar.get(Calendar.MONTH);

        criteria.and("year").is(year);
        criteria.and("month").gte(startMonth).lte(endMonth);
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
        if (StatisticLevel.site.equals(statisticLevel)) {
            fields = fields.and(Fields.fields("stationId", "stationName", "stationType", "operationState"));
        }
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group(fields)
                        .sum("durationSum").as("durationSum")
                        .count().as("siteCount"));
        AggregationResults<JSONObject> results = mongoTemplate.aggregate(aggregation, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_STATION_BREAK + taskId, JSONObject.class);
        List<JSONObject> resultCount = results.getMappedResults();
        criteria.and("operationState").is(FsuOperationState.MAINTENANCE);
        aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group(fields)
//                        .sum("durationSum").as("durationSum")
                        .count().as("siteCount"));
        results = mongoTemplate.aggregate(aggregation, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_STATION_BREAK + taskId, JSONObject.class);
        List<JSONObject> operationStateResults = results.getMappedResults();
        Map<String, Integer> operationStateCountMap = new HashMap<>();
        for (JSONObject jsonObject: operationStateResults ){
            Integer siteCount = jsonObject.getInteger("siteCount");
            operationStateCountMap.put(CommonCheck.aggregateTierName(jsonObject), siteCount);
        }
        for (JSONObject result : resultCount) {
            String tierName = CommonCheck.aggregateTierName(result);
            Integer maintenanceSCount = operationStateCountMap.get(tierName);
            result.put("maintenanceSCount", maintenanceSCount);
        }

        return resultCount;
    }

    public void updateDelete(int year, int month, String reportTaskId) {
        Query query = Query.query(Criteria.where("year").is(year).and("month").is(month));
        Update update = Update.update("deleteFlag", true);
        mongoTemplate.updateMulti(query, update, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_STATION_BREAK + reportTaskId);
    }
}