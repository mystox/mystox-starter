package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.StatisticLevel;
import com.kongtrolink.framework.reports.entity.TimePeriod;
import com.kongtrolink.framework.reports.entity.alarmCategory.AlarmCategoryTemp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/24 9:26
 * \* Description:
 * \
 */
@Service
public class AlarmCategoryTempDao extends MongoBaseDao {


    public void save(List<AlarmCategoryTemp> alarmCategoryTemps, String taskId) {
        mongoTemplate.insert(alarmCategoryTemps, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_CATEGORY_COUNT + taskId);
    }


    public void updateDelete(int year, int month, String taskId) {
        Query query = Query.query(Criteria.where("year").is(year).and("month").is(month));
        Update update = Update.update("deleteFlag", true);
        mongoTemplate.updateMulti(query, update, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_CATEGORY_COUNT + taskId);
    }

    public List<JSONObject> getCategoryDataByCondition(String taskId, JSONObject condition, TimePeriod timePeriod) {
        Criteria criteria = Criteria.where("deleteFlag").is(false);

        String alarmLevel = condition.getString("alarmLevel");
        if (!"全部".equals(alarmLevel) && StringUtils.isNotBlank(alarmLevel)) criteria.and("alarmLevel").is(alarmLevel);
        String stationType = condition.getString("stationType");
        if (!"全部".equals(stationType) && StringUtils.isNotBlank(stationType))
            criteria.and("stationType").is(stationType);
        String runningSate = condition.getString("operationState");
        if (!"全部".equals(runningSate) && StringUtils.isNotBlank(runningSate))
            criteria.and("operationState").is(runningSate);
        String fsuManufactory = condition.getString("fsuManufactory");
        if (!"全部".equals(fsuManufactory) && StringUtils.isNotBlank(fsuManufactory))
            criteria.and("fsuManufactory").is(fsuManufactory);
        JSONArray stationList = condition.getJSONArray("stationList");
        if (!CollectionUtils.isEmpty(stationList)) {
            List<String> siteIdList = stationList.toJavaList(String.class);
            criteria.and("stationId").in(siteIdList);
        }

        if ("current".equals(timePeriod.getDimension())) {
            criteria.and("year").is(0);
        } else {
            Date startTime = timePeriod.getStartTime();
            Date endTime = timePeriod.getEndTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            int year = calendar.get(Calendar.YEAR);
            int startMonth = calendar.get(Calendar.MONTH) + 1;
            calendar.setTime(endTime);
            int endMonth = calendar.get(Calendar.MONTH);
            if (endMonth == 0) endMonth = 12;
            criteria.and("year").is(year);
            criteria.and("month").gte(startMonth).lte(endMonth);
        }


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
        } /*else { //单站平均告警

        }*/
        Fields fields2 = fields.and(Fields.fields("province", "municipality", "county", "stationId", "stationName", "stationType"));
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group(fields2)
                        .sum("alarmCount").as("alarmCount")
                        .sum("fsuOffline").as("fsuOffline")
                        .sum("smokeSensation").as("smokeSensation")
                        .sum("sensirion").as("sensirion")
                        .sum("switchPower").as("switchPower")
                        .sum("battery").as("battery")
                        .sum("infrared").as("infrared")
                        .sum("gateMagnetism").as("gateMagnetism")
                        .sum("waterImmersion").as("waterImmersion")
                        .sum("airConditioning").as("airConditioning"),
                Aggregation.group(fields)
                        .sum("alarmCount").as("alarmCount")
                        .sum("fsuOffline").as("fsuOffline")
                        .sum("smokeSensation").as("smokeSensation")
                        .sum("sensirion").as("sensirion")
                        .sum("switchPower").as("switchPower")
                        .sum("battery").as("battery")
                        .sum("infrared").as("infrared")
                        .sum("gateMagnetism").as("gateMagnetism")
                        .sum("waterImmersion").as("waterImmersion")
                        .sum("airConditioning").as("airConditioning")
                        .avg("alarmCount").as("countAvg")
                        .first("province").as("province"),
                sort(Sort.Direction.ASC, "province"));
        AggregationResults<JSONObject> results = mongoTemplate.aggregate(aggregation, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_CATEGORY_COUNT + taskId, JSONObject.class);
        List<JSONObject> mappedResults = results.getMappedResults();
        return mappedResults;
    }
}