package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.StatisticLevel;
import com.kongtrolink.framework.reports.entity.TimePeriod;
import com.kongtrolink.framework.reports.entity.alarmCount.AlarmCountTemp;
import com.kongtrolink.framework.reports.entity.alarmCount.HomeAlarmCountTemp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * \* @Author: Mag
 * \* Date: 2020年4月8日 09:42:03
 * \* Description:
 * \ 首页需求：根据站点 按天统计历史告警数量
 * type: 1 = 近一个月告警频发站点Top10
 * type：2 = 近一个月告警频发Top8
 */
@Service
public class HomeAlarmCountTempDao extends MongoBaseDao {


    public List<JSONObject> getCountDataByCondition(String taskId, JSONObject condition, TimePeriod timePeriod) {
        long startTime = timePeriod.getStartTime().getTime();
        long endTime = timePeriod.getEndTime().getTime();
        Criteria criteria = Criteria.where("tempDate").gte(startTime).lte(endTime);
        JSONArray stationList = condition.getJSONArray("stationList");
        List<String> siteIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(stationList)) {
             siteIdList = stationList.toJavaList(String.class);
        }
        criteria.and("stationId").in(siteIdList);
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
            fields = fields.and(Fields.fields("stationName"));
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                group(fields).sum("alarmCount").as("alarmCount"),
                sort(Sort.Direction.DESC, "alarmCount"),
                limit(10)
        );
        AggregationResults<JSONObject> results = mongoTemplate.aggregate(aggregation, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_HomeALARM_COUNT + taskId, JSONObject.class);
        List<JSONObject> mappedResults = results.getMappedResults();
        return mappedResults;
    }

    public void save(List<HomeAlarmCountTemp> alarmCountTemps, String taskId) {
        mongoTemplate.insert(alarmCountTemps, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_HomeALARM_COUNT + taskId);
    }

    public void updateDelete(String time, String taskId) {
        Query query = Query.query(Criteria.where("time").is(time));
        mongoTemplate.remove(query, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_HomeALARM_COUNT + taskId);
    }
}