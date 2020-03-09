package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.SystemReports.StationBreakTemp;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public List<JSONObject> getStationBreakByCondition(String taskId, List<JSONObject> jsonObjects, String period, JSONObject timePeriod) {
//        mongoTemplate.find();
        return null;
    }
}