package com.kongtrolink.framework.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.query.AlarmQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/11 15:52
 * \* Description:
 * \
 */

@Repository
public class AlarmReduceDao {
    Logger logger = LoggerFactory.getLogger(AlarmReduceDao.class);
    private static final String current_table = MongTable.ALARM_CURRENT;
    private static final String history_table = MongTable.ALARM_HISTORY;
    @Autowired
    MongoTemplate mongoTemplate;

    public List<JSONObject> AlarmCount(AlarmQuery alarmQuery) {

        String tablePrefix = alarmQuery.getEnterpriseCode() + Contant.UNDERLINE + alarmQuery.getServerCode()
                + Contant.UNDERLINE + history_table + Contant.UNDERLINE;
        //2，获取时间跨度内各个时间点的年周数，生成对应的表
        List<String> weeks = getWeeks(alarmQuery);
        //从各个表获取数据
        String map = "function(){\n" +
                "emit(this.targetLevelName,1);\n" +
                "};";
        String reduce = "function(key,values){\n" +
                "   \n" +
                "    return  Array.sum(values);\n" +
                "};";
        List<JSONObject> alarmCounts = new ArrayList<>();
        Map<String, Long> countMap = new HashMap<>();
        Map<String, Long> recoverMap = new HashMap<>();
        for (int i = weeks.size() - 1; i >= 0; i--) {
            String week = weeks.get(i);
            logger.debug("map reduce weeks data:[{}]", week);
            String table = tablePrefix + week;
            Criteria criteria = new Criteria();
            criteria = commonQuery(criteria, alarmQuery);
            groupCountReduce(criteria, table, map, reduce, countMap);
            criteria.and("state").is("已消除");
            groupCountReduce(criteria, table, map, reduce, recoverMap);
//            logger.debug("map reduce weeks result:[{}]", JSONObject.toJSONString(countMap) + JSONObject.toJSONString(recoverMap));
        }
        countMap.forEach((k, v) -> {
            JSONObject entity = new JSONObject();
            entity.put("name", k);
            entity.put("count", v);
            entity.put("recoverCount", recoverMap.get(k));
            alarmCounts.add(entity);
        });

        logger.info("map reduce result:[{}]", JSONObject.toJSONString(countMap) + JSONObject.toJSONString(recoverMap));

        return alarmCounts;

    }

    private void groupCountReduce(Criteria criteria, String table, String map, String reduce, Map<String, Long> countMap) {
        boolean b = mongoTemplate.collectionExists(table);
        if (!b) return;
        MapReduceResults<JSONObject> mapReduceResults = mongoTemplate.mapReduce(
                Query.query(criteria), table, map, reduce, JSONObject.class);
            logger.debug("table:{}, reduceResult:[{}]",table,JSONObject.toJSONString(mapReduceResults.iterator()));
        for (JSONObject mapResult : mapReduceResults) {
            String key = mapResult.getString("_id");
            Long value = mapResult.getLong("value");
            Long count = countMap.get(key);

            if (count != null)
                count += value;
            else
                count = value;
            countMap.put(key, count);
        }
    }

    private Criteria commonQuery(Criteria criteria, AlarmQuery alarmQuery) {
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        if (!StringUtil.isNUll(enterpriseCode)) {
            criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String serverCode = alarmQuery.getServerCode();
        if (!StringUtil.isNUll(serverCode)) {
            criteria.and("serverCode").is(serverCode);
        }
        List<String> deviceIdList = alarmQuery.getDeviceIdList();
        if (deviceIdList != null && deviceIdList.size() > 0)
            criteria.and("deviceId").in(deviceIdList);
        Date startBeginTime = alarmQuery.getStartBeginTime();
        Date startEndTime = alarmQuery.getStartEndTime();
        if (null != startBeginTime && null == startEndTime) {
            criteria.and("treport").gte(startBeginTime);
        } else if (null != startBeginTime && null != startEndTime) {
            criteria.and("treport").gte(startBeginTime).lte(startEndTime);
        } else if (null == startBeginTime && null != startEndTime) {
            criteria.and("treport").lte(startEndTime);
        }
        return criteria;


    }

    private List<String> getWeeks(AlarmQuery alarmQuery) {
        List<String> weekList = new ArrayList<>();
        Date startBeginTime = alarmQuery.getStartBeginTime();
        Date startEndTime = alarmQuery.getStartEndTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startBeginTime);
        while (startBeginTime.getTime() <= startEndTime.getTime()) {
            String year_week = DateUtil.getYear_week(startBeginTime);
            if (!weekList.contains(year_week)) {
                weekList.add(year_week);
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            startBeginTime = calendar.getTime();
        }
        String endWeek = DateUtil.getYear_week(startEndTime);
        if (!weekList.contains(endWeek)) {
            weekList.add(endWeek);
        }
        return weekList;
    }


    class alarmCountEntity {
        private String alarmLevelName;
        private Long count;
        private Long recoverCount;

        public String getAlarmLevelName() {
            return alarmLevelName;
        }

        public void setAlarmLevelName(String alarmLevelName) {
            this.alarmLevelName = alarmLevelName;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public Long getRecoverCount() {
            return recoverCount;
        }

        public void setRecoverCount(Long recoverCount) {
            this.recoverCount = recoverCount;
        }
    }

}