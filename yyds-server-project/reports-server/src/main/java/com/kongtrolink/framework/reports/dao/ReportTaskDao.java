package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.ReportTask;
import com.kongtrolink.framework.reports.entity.TaskStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by mystoxlol on 2019/10/23, 11:15.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ReportTaskDao extends MongoBaseDao {


    public void save(ReportTask reportTask) {
        mongoTemplate.save(reportTask, MongoDocName.REPORT_TASK);
    }

    public List<ReportTask> findReportTask(JSONObject query) {
        int count = query.get("count") == null ? 30 : (int) query.get("count");
        int page = query.get("page") == null ? 1 : (int) query.get("page");
        Criteria criteria = new Criteria();

        String serverCode = query.getString("serverCode");
        if (StringUtils.isNotBlank(serverCode)) {
            criteria = criteria.and("serverCode").is(serverCode);
        }
        String enterpriseCode = query.getString("enterpriseCode");
        if (StringUtils.isNotBlank(enterpriseCode)) {
            criteria = criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String operaCode = query.getString("operaCode");
        if (StringUtils.isNotBlank(operaCode)) {
            criteria = criteria.and("operaCode").regex(operaCode);
        }
        Integer taskStatus = query.getInteger("taskStatus");
        if (taskStatus != null) {
            criteria = criteria.and("taskStatus").is(taskStatus);
        }
        String taskType = query.getString("taskType");
        if (StringUtils.isNotBlank(taskType)) {
            criteria = criteria.and("taskType").is(taskType);
        }
        String reportServerCode = query.getString("reportServerCode");
        if (StringUtils.isNotBlank(reportServerCode)) {
            criteria = criteria.and("reportServerCode").regex(reportServerCode);
        }
        String reportName = query.getString("reportName");
        if (StringUtils.isNotBlank(reportName)) {
            criteria = criteria.and("reportName").regex(reportName);
        }
        return mongoTemplate.find(Query.query(criteria).skip((page - 1) * count).limit(count), ReportTask.class, MongoDocName.REPORT_TASK);
    }

    public long findReportTaskCount(JSONObject query) {
//        int count = query.get("count") == null ? 30 : (int) query.get("count");
//        int page = query.get("page") == null ? 1 : (int) query.get("page");
        Criteria criteria = new Criteria();

        String serverCode = query.getString("serverCode");
        if (StringUtils.isNotBlank(serverCode)) {
            criteria = criteria.and("serverCode").is(serverCode);
        }
        String enterpriseCode = query.getString("enterpriseCode");
        if (StringUtils.isNotBlank(enterpriseCode)) {
            criteria = criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String operaCode = query.getString("operaCode");
        if (StringUtils.isNotBlank(operaCode)) {
            criteria = criteria.and("operaCode").regex(operaCode);
        }
        String taskType = query.getString("taskType");
        if (StringUtils.isNotBlank(taskType)) {
            criteria = criteria.and("taskType").is(taskType);
        }
        Integer taskStatus = query.getInteger("taskStatus");
        if (taskStatus != null) {
            criteria = criteria.and("taskStatus").is(taskStatus);
        }
        String reportServerCode = query.getString("reportServerCode");
        if (StringUtils.isNotBlank(reportServerCode)) {
            criteria = criteria.and("reportServerCode").regex(reportServerCode);
        }
        String reportName = query.getString("reportName");
        if (StringUtils.isNotBlank(reportName)) {
            criteria = criteria.and("reportName").regex(reportName);
        }
        return mongoTemplate.count(Query.query(criteria), MongoDocName.REPORT_TASK);
    }


    public boolean isExistsByOperaCode(String serverCode, String enterpriseCode, String operaCode, String reportServerCode) {

        return mongoTemplate.exists(Query.query(
                Criteria.where("serverCode").is(serverCode)
                        .and("enterpriseCode").is(enterpriseCode)
                        .and("operaCode").is(operaCode)
                        .and("reportServerCode").is(reportServerCode)
        ), MongoDocName.REPORT_TASK);
    }

    public ReportTask findExecuteReportTask(String reportServerCode) {
        return mongoTemplate.findAndModify(Query.query(Criteria.where("taskStatus").is(1)
                        .and("reportServerCode").is(reportServerCode)
//                        .and("taskType").is(TaskType.schecduled)
                        .and("startTime").lte(new Date())
                ), Update.update("taskStatus", TaskStatus.RUNNING.getStatus()),
                ReportTask.class, MongoDocName.REPORT_TASK);
    }

    /**
     * 任务组合查询唯一条件
     *
     * @param serverCode
     * @param enterpriseCode
     * @param operaCode
     * @return
     */
    public ReportTask findByByUniqueCondition(String serverCode, String enterpriseCode, String operaCode, String reportServerCode) {

        return mongoTemplate.findOne(Query.query(
                Criteria.where("serverCode").is(serverCode)
                        .and("enterpriseCode").is(enterpriseCode)
                        .and("operaCode").is(operaCode)
                        .and("reportServerCode").is(reportServerCode)
        ), ReportTask.class, MongoDocName.REPORT_TASK);
    }


    public List<ReportTask> findRunningReportTask(String reportServerCode) {
        return mongoTemplate.find(Query.query(
                Criteria.where("taskStatus").is(2)
                        .and("reportServerCode").is(reportServerCode)
        ), ReportTask.class, MongoDocName.REPORT_TASK);
    }
}
