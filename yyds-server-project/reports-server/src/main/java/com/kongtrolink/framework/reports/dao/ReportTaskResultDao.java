package com.kongtrolink.framework.reports.dao;

import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.ReportTask;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/24, 19:05.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ReportTaskResultDao extends MongoBaseDao{

    public void save(ReportTask reportTask) {
        mongoTemplate.save(reportTask, MongoDocName.REPORT_TASK_RESULT);
    }

}
