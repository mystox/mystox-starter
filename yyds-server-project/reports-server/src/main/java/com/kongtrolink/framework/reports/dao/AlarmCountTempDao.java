package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.alarmCount.AlarmCountTemp;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/24 9:26
 * \* Description:
 * \
 */
@Service
public class AlarmCountTempDao extends MongoBaseDao {


    public List<JSONObject> getCountDataByCondition(String taskId, List<JSONObject> siteList, String alarmLevel, String statisticLevel, String period, JSONObject timePeriod) {

        return null;
//        mongoTemplate.aggregate()
    }

    public void save(List<AlarmCountTemp> alarmCountTemps,String taskId) {
        mongoTemplate.insert(alarmCountTemps, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_ALARM_COUNT+taskId);
    }



}