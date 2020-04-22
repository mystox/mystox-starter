package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.StatisticLevel;
import com.kongtrolink.framework.reports.entity.TimePeriod;
import com.kongtrolink.framework.reports.entity.fsu.FsuRunStateTemp;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
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
        mongoTemplate.insert(fsuRunStateTemps, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_FSU_RUNNING_STATE + taskId);
    }

    public void updateDelete(int year, int month, String taskId) {
        Query query = Query.query(Criteria.where("year").is(year).and("month").is(month));
        Update update = Update.update("deleteFlag", true);
        mongoTemplate.updateMulti(query, update, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_FSU_RUNNING_STATE + taskId);
    }


    public List<JSONObject> getFsuRunStateData(String taskId, JSONObject condition, TimePeriod timePeriod) {

        Criteria criteria = Criteria.where("deleteFlag").is(false);

        String stationType = condition.getString("stationType");
        if (!"全部".equals(stationType)) criteria.and("stationType").is(stationType);
        String runningSate = condition.getString("runningSate");
        if (!"全部".equals(runningSate)) criteria.and("runningSate").is(runningSate);
        String fsuManufactory = condition.getString("fsuManufactory");
        if (!"全部".equals(fsuManufactory)) criteria.and("fsuManufactory").is(fsuManufactory);
        JSONArray stationList = condition.getJSONArray("stationList");
        if (!CollectionUtils.isEmpty(stationList)) {
            List<String> siteIdList = stationList.toJavaList(String.class);
            criteria.and("stationId").in(siteIdList);
        }
        if (!CollectionUtils.isEmpty(stationList)) {
            List<String> siteIdList = stationList.toJavaList(String.class);
            criteria.and("stationId").in(siteIdList);
        }
        Date startTime = timePeriod.getStartTime();
        Date endTime = timePeriod.getEndTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        int year = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH) + 1;
        calendar.setTime(endTime);
        int endMonth = calendar.get(Calendar.MONTH);
        if (endMonth == 0) endMonth = 12;
        if (endMonth < startMonth) endMonth += 1;
        criteria.and("year").is(year);
        criteria.and("month").gte(startMonth).lte(endMonth);
        String statisticLevel = condition.getString("statisticLevel");

        Fields fields = Fields.fields();
        if (StatisticLevel.province.equals(statisticLevel)) {
            fields = fields.and("province");
        }
        if (StatisticLevel.municipality.equals(statisticLevel)) {
            fields = fields.and(Fields.fields("province", "municipality"));
        }
        if (StatisticLevel.county.equals(statisticLevel)) {
            fields = fields.and(Fields.fields("province", "municipality", "county"));
        }
        if (StatisticLevel.site.equals(statisticLevel)) {
            fields = fields.and(Fields.fields("province", "municipality", "county", "stationId", "stationName", "stationType"));
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