package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.alarmCategory.AlarmCategoryTemp;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/24 9:26
 * \* Description:
 * \
 */
@Service
public class AlarmCategoryTempDao extends MongoBaseDao {


    public List<JSONObject> getCategoryDataByCondition(String taskId, List<JSONObject> siteList, String alarmLevel, String statisticLevel, String period, JSONObject timePeriod) {

        return null;
//        mongoTemplate.aggregate()
    }

    public void save(List<AlarmCategoryTemp> alarmCategoryTemps, String taskId) {
        mongoTemplate.insert(alarmCategoryTemps,MongoDocName.REPORT_OPERA_EXECUTE_TEMP_CATEGORY_COUNT+taskId);
    }



}