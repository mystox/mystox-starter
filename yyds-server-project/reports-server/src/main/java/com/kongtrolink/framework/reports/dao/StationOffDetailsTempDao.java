package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.SystemReports.StationOffDetailsTemp;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/3 14:58
 * \* Description:
 * \
 */
@Service
public class StationOffDetailsTempDao extends MongoBaseDao {

    public void save(List<StationOffDetailsTemp> StationOffDetailsTemps, String taskId) {
        mongoTemplate.insert(StationOffDetailsTemps, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_STATION_OFFLINE_DETAILS+taskId);
    }

    public List<JSONObject> getStationOffDetailsByCondition(String taskId, List<JSONObject> siteList, String period, JSONObject timePeriod) {


        return null;
//        mongoTemplate.aggregate()
    }


}