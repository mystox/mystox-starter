package com.kongtrolink.framework.reports.dao;

import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.fsuOfflineDetails.FsuOfflineDetailsTemp;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/2 10:39
 * \* Description:
 * \
 */
@Service
public class FsuOfflineDetailsTempDao extends MongoBaseDao {


    public void save(List<FsuOfflineDetailsTemp> alarmCountTemps, String taskId) {
        mongoTemplate.insert(alarmCountTemps, MongoDocName.REPORT_OPERA_EXECUTE_TEMP_FSU_OFFLINE_DETAILS+taskId);
    }

}