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
        List<String> deviceIdList = alarmQuery.getDeviceIds();
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

    /**
     * @Date 9:09 2020/3/16
     * @Param No such property: code for class: Script1
     * @return java.util.List<com.alibaba.fastjson.JSONObject>
     * @Author mystox
     * @Description //获取一个月内历史告警
     **/
    public List<JSONObject> getAlarmHistory(AlarmQuery alarmQuery) {
        String tablePrefix = alarmQuery.getEnterpriseCode() + Contant.UNDERLINE + alarmQuery.getServerCode()
                + Contant.UNDERLINE + history_table + Contant.UNDERLINE;
        //2，获取时间跨度内各个时间点的年周数，生成对应的表
        List<String> weeks = getWeeks(alarmQuery);
        List<JSONObject> alarmHistory = new ArrayList<>();
        for (int i = weeks.size() - 1; i >= 0; i--) {
            String week = weeks.get(i);
            logger.debug("get alarmHistory weeks data:[{}]", week);
            String table = tablePrefix + week;
            Criteria criteria = new Criteria();
            criteria = commonQuery(criteria, alarmQuery);
            List<JSONObject> jsonObjects = mongoTemplate.find(Query.query(criteria), JSONObject.class, table);
            alarmHistory.addAll(jsonObjects);
        }
        logger.info("get alarm history count is [{}]", alarmHistory.size());
        return alarmHistory;
    }

    public List<JSONObject> getAlarmCategory(AlarmQuery alarmQuery) {
        String tablePrefix = alarmQuery.getEnterpriseCode() + Contant.UNDERLINE + alarmQuery.getServerCode()
                + Contant.UNDERLINE + history_table + Contant.UNDERLINE;
        //2，获取时间跨度内各个时间点的年周数，生成对应的表
        List<String> weeks = getWeeks(alarmQuery);
        //从各个表获取数据
        String map = "function(){\n" +
                "        var type = this.deviceInfos.type + '';\n" +
                "        emit(this.targetLevelName,\n" +
                "        {arr:[{deviceType:type,count:1}]}\n" +
                "        )}";
        String reduce = "function (key,values){\n" +
                "            var ret = {arr:[]};\n" +
                "            var deviceTypeMap = {};\n" +
                "            for(var i = 0; i<values.length;i++){\n" +
                "                var ia = values[i];\n" +
                "                for(var j in ia.arr){\n" +
                "                    var deviceType = ia.arr[j].deviceType;\n" +
                "                    var count = ia.arr[j].count;\n" +
                "                    deviceType in deviceTypeMap?deviceTypeMap[deviceType] += count:deviceTypeMap[deviceType] = count;\n" +
                "                    }\n" +
                "                }\n" +
                "            for(var d in deviceTypeMap){\n" +
                "                var entity = {deviceType:d,count:deviceTypeMap[d]};\n" +
                "                ret.arr.push(entity);\n" +
                "            }\n" +
                "            return ret;\n" +
                "        }";
        List<JSONObject> alarmCategoryList = new ArrayList<>();
        Map<String, JSONObject> resultMap = new HashMap<>();
        for (int i = weeks.size() - 1; i >= 0; i--) {
            String week = weeks.get(i);
            logger.debug("map reduce weeks data:[{}]", week);
            String table = tablePrefix + week;
            Criteria criteria = new Criteria();
            criteria = commonQuery(criteria, alarmQuery);
            boolean b = mongoTemplate.collectionExists(table);
            if (!b) continue;

            MapReduceResults<JSONObject> mapReduceResults = mongoTemplate.mapReduce(
                    Query.query(criteria), table, map, reduce, JSONObject.class);
            logger.debug("table:{}, reduceResult:[{}]",table,JSONObject.toJSONString(mapReduceResults.iterator()));
            for (JSONObject result : mapReduceResults)
            {
                String levelName = result.getString("_id");
                JSONObject deviceMap = resultMap.get(levelName);
                List<JSONObject> arr = result.getJSONArray("arr").toJavaList(JSONObject.class);
                long sum = 0L;
                for (JSONObject deviceCount: arr)
                {
                    String deviceType = deviceCount.getString("deviceType");
                    Long count = deviceCount.getLong("count");
                    if (count==null) count = 0L;
                    sum += count;
                    Long countOld = deviceMap.getLong(deviceType);
                    if (countOld!=null) countOld += count;
                    else countOld = count;
                    deviceMap.put(deviceType, countOld);
                }
                Long sumOld = deviceMap.getLong("sum");
                sumOld += sum;
                deviceMap.put("sum", sumOld);
            }

        }
        resultMap.forEach((alarmLevel, deviceMap) -> {
            JSONObject entity = new JSONObject();
            entity.put("alarmLevel", alarmLevel);
            entity.put("alarmCount", deviceMap.getLong("sum"));
            entity.put("fsuOffline", 0L); //todo fsu离线告警待实现
            Long smokeSensation = deviceMap.getLong("烟感");
            entity.put("smokeSensation", smokeSensation);
            Long sensirion = deviceMap.getLong("温湿度");
            entity.put("sensirion", sensirion);
            Long switchPower = deviceMap.getLong("开关电源");
            entity.put("switchPower", switchPower);
            Long battery = deviceMap.getLong("蓄电池");
            entity.put("battery", battery);
            Long infrared = deviceMap.getLong("红外设备");
            entity.put("infrared", infrared);
            Long gateMagnetism = deviceMap.getLong("门磁");
            entity.put("gateMagnetism", gateMagnetism);
            Long waterImmersion = deviceMap.getLong("水浸");
            entity.put("waterImmersion", waterImmersion);
            Long airConditioning = deviceMap.getLong("空调");
            entity.put("airConditioning", airConditioning);
            alarmCategoryList.add(entity);
        });

        logger.info("map reduce result:[{}]", JSONObject.toJSONString(alarmCategoryList));
        return alarmCategoryList;

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