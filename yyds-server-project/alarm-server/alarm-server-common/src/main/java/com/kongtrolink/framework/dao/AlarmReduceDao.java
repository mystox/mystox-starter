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
        logger.debug("table:{}, reduceResult:[{}]", table, JSONObject.toJSONString(mapReduceResults.iterator()));
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
        String name = alarmQuery.getName();
        if (!StringUtil.isNUll(name)) {
            criteria.and("name").is(name);
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
     * @return java.util.List<com.alibaba.fastjson.JSONObject>
     * @Date 9:09 2020/3/16
     * @Param No such property: code for class: Script1
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
    public List<JSONObject> getAlarmCurrent(AlarmQuery alarmQuery) {
        String table = "alarm_current";
        Criteria criteria = new Criteria();
        criteria = commonQuery(criteria, alarmQuery);
        List<JSONObject> jsonObjects = mongoTemplate.find(Query.query(criteria), JSONObject.class, table);
        return jsonObjects;
    }
    public List<JSONObject> getAlarmCategory(AlarmQuery alarmQuery) {
        String tablePrefix = alarmQuery.getEnterpriseCode() + Contant.UNDERLINE + alarmQuery.getServerCode()
                + Contant.UNDERLINE + history_table + Contant.UNDERLINE;
        //2，获取时间跨度内各个时间点的年周数，生成对应的表
        List<String> weeks = getWeeks(alarmQuery);
        //从各个表获取数据
        String keyName = "FSU离线告警";
        String map = "function(){\n" +
                "        var type = this.deviceInfos.type;\n" +
                "        emit(this.targetLevelName,\n" +
                "        {arr:[{deviceType:type,count:1}],name:this.name,fsuOfflineCount:1}\n" +
                "        )}";
        String reduce = "function (key,values){\n" +
                "            var keyName = '" + keyName + "';\n" +
                "            var ret = {arr:[]};\n" +
                "            var deviceTypeMap = {};\n" +
                "            var fsuOfflineCountTemp=0;\n" +
                "            for(var i = 0; i<values.length;i++){\n" +
                "                var ia = values[i];\n" +
                "                for(var j in ia.arr){\n" +
                "                    var deviceType = ia.arr[j].deviceType;\n" +
                "                    var count = ia.arr[j].count;\n" +
                "                    deviceType in deviceTypeMap?deviceTypeMap[deviceType] += count:deviceTypeMap[deviceType] = count;\n" +
                "                    }\n" +
                "                var name = ia.name;\n" +
                "                var fsuOfflineCount = ia.fsuOfflineCount;\n" +
                "                if(name == keyName) fsuOfflineCountTemp += fsuOfflineCount;\n" +
                "                \n" +
                "            }\n" +
                "            ret['name'] = keyName;\n" +
                "            ret['fsuOfflineCount'] = fsuOfflineCountTemp;\n" +
                "            for(var d in deviceTypeMap){\n" +
                "                var entity = {deviceType:d,count:deviceTypeMap[d]};\n" +
                "                ret.arr.push(entity);\n" +
                "            }\n" +
                "//             if(key == '一级告警') {\n" +
                "//             print(JSON.stringify(values));\n" +
                "//             print('end------------- '+JSON.stringify(ret));\n" +
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
            logger.debug("table:{}, reduceResult:[{}]", table, JSONObject.toJSONString(mapReduceResults.iterator()));
            for (JSONObject result : mapReduceResults) {
                String levelName = result.getString("_id");
                JSONObject deviceMap = resultMap.get(levelName);
                if (deviceMap == null)
                    deviceMap = new JSONObject();
                resultMap.put(levelName, deviceMap);
                List<JSONObject> arr = result.getJSONObject("value").getJSONArray("arr").toJavaList(JSONObject.class);
                long sum = 0L;
                for (JSONObject deviceCount : arr) {
                    String deviceType = deviceCount.getString("deviceType");
                    Long count = deviceCount.getLong("count");
                    if (count == null) count = 0L;
                    sum += count;
                    Long countOld = deviceMap.getLong(deviceType);
                    if (countOld != null) countOld += count;
                    else countOld = count;
                    deviceMap.put(deviceType, countOld);
                }
                Long sumOld = deviceMap.getLong("sum");
                if (sumOld == null) sumOld = 0L;
                sumOld += sum;
                deviceMap.put("sum", sumOld);
            }

        }
        resultMap.forEach((alarmLevel, deviceMap) -> {
            JSONObject entity = new JSONObject();
            entity.put("alarmLevel", alarmLevel);
            entity.put("alarmCount", deviceMap.getLong("sum"));
            entity.put("fsuOffline", deviceMap.get("fsuOfflineCount")); // fsu离线告警
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
    public List<JSONObject> getAlarmCurrentCategory(AlarmQuery alarmQuery) {
        //从各个表获取数据
        String keyName = "FSU离线告警";
        String map = "function(){\n" +
                "        var type = this.deviceInfos.type;\n" +
                "        emit(this.targetLevelName,\n" +
                "        {arr:[{deviceType:type,count:1}],name:this.name,fsuOfflineCount:1}\n" +
                "        )}";
        String reduce = "function (key,values){\n" +
                "            var keyName = '" + keyName + "';\n" +
                "            var ret = {arr:[]};\n" +
                "            var deviceTypeMap = {};\n" +
                "            var fsuOfflineCountTemp=0;\n" +
                "            for(var i = 0; i<values.length;i++){\n" +
                "                var ia = values[i];\n" +
                "                for(var j in ia.arr){\n" +
                "                    var deviceType = ia.arr[j].deviceType;\n" +
                "                    var count = ia.arr[j].count;\n" +
                "                    deviceType in deviceTypeMap?deviceTypeMap[deviceType] += count:deviceTypeMap[deviceType] = count;\n" +
                "                    }\n" +
                "                var name = ia.name;\n" +
                "                var fsuOfflineCount = ia.fsuOfflineCount;\n" +
                "                if(name == keyName) fsuOfflineCountTemp += fsuOfflineCount;\n" +
                "                \n" +
                "            }\n" +
                "            ret['name'] = keyName;\n" +
                "            ret['fsuOfflineCount'] = fsuOfflineCountTemp;\n" +
                "            for(var d in deviceTypeMap){\n" +
                "                var entity = {deviceType:d,count:deviceTypeMap[d]};\n" +
                "                ret.arr.push(entity);\n" +
                "            }\n" +
                "//             if(key == '一级告警') {\n" +
                "//             print(JSON.stringify(values));\n" +
                "//             print('end------------- '+JSON.stringify(ret));\n" +
                "            return ret;\n" +
                "        }";
        List<JSONObject> alarmCategoryList = new ArrayList<>();
        Map<String, JSONObject> resultMap = new HashMap<>();
            String table = "alarm_current";
            Criteria criteria = new Criteria();
            criteria = commonQuery(criteria, alarmQuery);
            MapReduceResults<JSONObject> mapReduceResults = mongoTemplate.mapReduce(
                    Query.query(criteria), table, map, reduce, JSONObject.class);
            logger.debug("table:{}, reduceResult:[{}]", table, JSONObject.toJSONString(mapReduceResults.iterator()));
            for (JSONObject result : mapReduceResults) {
                String levelName = result.getString("_id");
                JSONObject deviceMap = resultMap.get(levelName);
                if (deviceMap == null)
                    deviceMap = new JSONObject();
                resultMap.put(levelName, deviceMap);
                List<JSONObject> arr = result.getJSONObject("value").getJSONArray("arr").toJavaList(JSONObject.class);
                long sum = 0L;
                for (JSONObject deviceCount : arr) {
                    String deviceType = deviceCount.getString("deviceType");
                    Long count = deviceCount.getLong("count");
                    if (count == null) count = 0L;
                    sum += count;
                    Long countOld = deviceMap.getLong(deviceType);
                    if (countOld != null) countOld += count;
                    else countOld = count;
                    deviceMap.put(deviceType, countOld);
                }
                Long sumOld = deviceMap.getLong("sum");
                if (sumOld == null) sumOld = 0L;
                sumOld += sum;
                deviceMap.put("sum", sumOld);
            }

        resultMap.forEach((alarmLevel, deviceMap) -> {
            JSONObject entity = new JSONObject();
            entity.put("alarmLevel", alarmLevel);
            entity.put("alarmCount", deviceMap.getLong("sum"));
            entity.put("fsuOffline", deviceMap.get("fsuOfflineCount")); // fsu离线告警
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

    public JSONObject fsuOfflineStatistic(AlarmQuery alarmQuery) {
        String tablePrefix = alarmQuery.getEnterpriseCode() + Contant.UNDERLINE + alarmQuery.getServerCode()
                + Contant.UNDERLINE + history_table + Contant.UNDERLINE;
        //2，获取时间跨度内各个时间点的年周数，生成对应的表
        List<String> weeks = getWeeks(alarmQuery);
        //从各个表获取数据
        String map = "function(){\n" +
                "        var duration = (this.trecover-this.treport)/1000/60;\n" +
                "        emit(1,{duration:duration,count:1})\n" +
                "        }";
        String reduce = "function (key,values){\n" +
                "            var ret = {};\n" +
                "            var duration = 0;\n" +
                "            var count = 0;\n" +
                "            for(var i = 0; i<values.length;i++){\n" +
                "                var value = values[i];\n" +
                "                duration += value.duration;\n" +
                "                count += value.count;\n" +
                "            }\n" +
                "            ret['duration'] = duration;\n" +
                "            ret['count'] = count;\n" +
                "            return ret;\n" +
                "        }";
        JSONObject fsuOfflineStatisticList = new JSONObject();
        for (int i = weeks.size() - 1; i >= 0; i--) {
            String week = weeks.get(i);
            logger.debug("map reduce weeks data:[{}]", week);
            String table = tablePrefix + week;
            Criteria criteria = new Criteria();
            criteria = commonQuery(criteria, alarmQuery);
            criteria.and("name").is("FSU离线告警");
            criteria.and("state").is("已消除");
            boolean b = mongoTemplate.collectionExists(table);
            if (!b) continue;
            MapReduceResults<JSONObject> mapReduceResults = mongoTemplate.mapReduce(
                    Query.query(criteria),
                    table, map, reduce, JSONObject.class);
            logger.debug("table:{}, reduceResult:[{}]", table, JSONObject.toJSONString(mapReduceResults.iterator()));
            for (JSONObject result : mapReduceResults) {
                JSONObject value = result.getJSONObject("value");
                Double duration = value.getDouble("duration");
                Integer count = value.getInteger("count");
                Double durationAvg = duration / count;
                fsuOfflineStatisticList.put("durationSum", duration);
                fsuOfflineStatisticList.put("count", count);
                fsuOfflineStatisticList.put("durationAvg", durationAvg);

            }

        }
        return fsuOfflineStatisticList;
    }

    public JSONObject stationOffStatistic(AlarmQuery alarmQuery) {
        String tablePrefix = alarmQuery.getEnterpriseCode() + Contant.UNDERLINE + alarmQuery.getServerCode()
                + Contant.UNDERLINE + history_table + Contant.UNDERLINE;
        //2，获取时间跨度内各个时间点的年周数，生成对应的表
        List<String> weeks = getWeeks(alarmQuery);
        //从各个表获取数据
        String map = "function(){\n" +
                "        var duration = (this.trecover-this.treport)/1000/60;\n" +
                "        emit(1,{duration:duration,count:1})\n" +
                "        }";
        String reduce = "function (key,values){\n" +
                "            var ret = {};\n" +
                "            var duration = 0;\n" +
                "            var count = 0;\n" +
                "            for(var i = 0; i<values.length;i++){\n" +
                "                var value = values[i];\n" +
                "                duration += value.duration;\n" +
                "                count += value.count;\n" +
                "            }\n" +
                "            ret['duration'] = duration;\n" +
                "            ret['count'] = count;\n" +
                "            return ret;\n" +
                "        }";
        JSONObject stationOffStatistic = new JSONObject();
        for (int i = weeks.size() - 1; i >= 0; i--) {
            String week = weeks.get(i);
            logger.debug("map reduce weeks data:[{}]", week);
            String table = tablePrefix + week;
            Criteria criteria = new Criteria();
            criteria = commonQuery(criteria, alarmQuery);
            criteria.and("name").and("交流输入XX停电告警");
            criteria.and("state").is("已消除");
            boolean b = mongoTemplate.collectionExists(table);
            if (!b) continue;
            MapReduceResults<JSONObject> mapReduceResults = mongoTemplate.mapReduce(
                    Query.query(criteria), table, map, reduce, JSONObject.class);
            logger.debug("table:{}, reduceResult:[{}]", table, JSONObject.toJSONString(mapReduceResults));
            for (JSONObject result : mapReduceResults) {
                JSONObject value = result.getJSONObject("value");
                Double duration = value.getDouble("duration");
                Integer count = value.getInteger("count");
                stationOffStatistic.put("durationSum", duration);
                stationOffStatistic.put("count", count);
            }

        }
        return stationOffStatistic;
    }

    public JSONObject stationBreakStatistic(AlarmQuery alarmQuery) {
        String tablePrefix = alarmQuery.getEnterpriseCode() + Contant.UNDERLINE + alarmQuery.getServerCode()
                + Contant.UNDERLINE + history_table + Contant.UNDERLINE;
        //2，获取时间跨度内各个时间点的年周数，生成对应的表
        List<String> weeks = getWeeks(alarmQuery);
        //从各个表获取数据
        String map = "function(){\n" +
                "        var duration = (this.trecover-this.treport)/1000/60;\n" +
                "        emit(1,{duration:duration,count:1})\n" +
                "        }";
        String reduce = "function (key,values){\n" +
                "            var ret = {};\n" +
                "            var duration = 0;\n" +
                "            for(var i = 0; i<values.length;i++){\n" +
                "                var value = values[i];\n" +
                "                duration += value.duration;\n" +
                "                count += value.count;\n" +
                "            }\n" +
                "            ret['duration'] = duration;\n" +
                "            return ret;\n" +
                "        }";

        JSONObject stationBreakStatistic = new JSONObject();
        for (int i = weeks.size() - 1; i >= 0; i--) {
            String week = weeks.get(i);
            logger.debug("map reduce weeks data:[{}]", week);
            String table = tablePrefix + week;
            Criteria criteria = new Criteria();
            criteria = commonQuery(criteria, alarmQuery);
            criteria.and("name").and("一级低压脱离告警");
            criteria.and("state").is("已消除");
            boolean b = mongoTemplate.collectionExists(table);
            if (!b) continue;
            MapReduceResults<JSONObject> mapReduceResults = mongoTemplate.mapReduce(
                    Query.query(criteria), table, map, reduce, JSONObject.class);
            logger.debug("table:{}, reduceResult:[{}]", table, JSONObject.toJSONString(mapReduceResults));
            for (JSONObject result : mapReduceResults) {
                JSONObject value = result.getJSONObject("value");
                Double duration = value.getDouble("duration");
                stationBreakStatistic.put("durationSum", duration);
            }

        }


        return stationBreakStatistic;
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