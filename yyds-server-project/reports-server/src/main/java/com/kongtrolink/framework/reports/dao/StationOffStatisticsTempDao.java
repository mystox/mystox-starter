package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.SystemReports.StationOffStatisticsTemp;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/3 14:58
 * \* Description:
 * \
 */
@Service
public class StationOffStatisticsTempDao extends MongoBaseDao {

    public void save(List<StationOffStatisticsTemp> stationOffStatisticsTemps, String taskId) {
        mongoTemplate.insert(stationOffStatisticsTemps, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_STATION_OFFLINE_STATISTIC+taskId);
    }

    public List<JSONObject> getStationOffStatisticsByCondition(String taskId, List<JSONObject> siteList,  String statisticLevel, String period, JSONObject timePeriod) {


        return null;
//        mongoTemplate.aggregate()
    }


}