package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.ReportTaskResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by mystoxlol on 2019/10/24, 19:05.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ReportTaskResultDao extends MongoBaseDao{

    public void save(ReportTaskResult reportTaskResult) {
        mongoTemplate.save(reportTaskResult, MongoDocName.REPORT_TASK_RESULT);
    }


    public ReportTaskResult findNewByTaskId(String taskId) {
        Query query = Query.query(Criteria.where("taskId").is(taskId));
        query.limit(1);
        query.with(new Sort(Sort.Direction.ASC, "recordTime"));
        return mongoTemplate.findOne(query, ReportTaskResult.class, MongoDocName.REPORT_TASK_RESULT);
    }

    public List<ReportTaskResult> findReportsTaskResult(JSONObject query) {
        int count = query.get("count") == null ? 30 : (int) query.get("count");
        int page = query.get("page") == null ? 1 : (int) query.get("page");
        String taskId = query.getString("taskId");
        return mongoTemplate.find(Query.query(Criteria.where("taskId").is(taskId))
                        .with(new Sort(new Sort.Order(Sort.Direction.DESC,"recordTime")))
                        .skip((page - 1) * count).limit(count),
                ReportTaskResult.class, MongoDocName.REPORT_TASK_RESULT);
    }

    public long findReportTaskResultCount(JSONObject body) {
        String taskId = body.getString("taskId");
        return mongoTemplate.count(Query.query(Criteria.where("taskId").is(taskId)),
                 MongoDocName.REPORT_TASK_RESULT);
    }


}
