package com.kongtrolink.framework.reports.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.ReportTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

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
        mongoTemplate.save(reportTask,MongoDocName.REPORT_TASK);

    }

    public List<ReportTask> findReportTask(JSONObject query) {

        int count = query.get("count") == null ? 30 : (int) query.get("count");
        int page = query.get("page") == null ? 1 : (int) query.get("page");
        Criteria criteria = new Criteria();
        String taskType = query.getString("taskType");
        if (StringUtils.isNotBlank(taskType)) {
            criteria = criteria.and("taskType").regex(taskType);
        }

        return mongoTemplate.find(Query.query(criteria).skip((page - 1) * count).limit(count), ReportTask.class, MongoDocName.REPORT_TASK);
    }


    public boolean isExistsByOperaCode(String serverCode, String enterpriseCode, String operaCode,String reportServerVersion) {

        return mongoTemplate.exists(Query.query(
                Criteria.where("serverCode").is(serverCode)
                        .and("enterpriseCode").is(enterpriseCode)
                        .and("operaCode").is(operaCode)
                .and("reportServerVersion").is(reportServerVersion)
        ), MongoDocName.REPORT_TASK);
    }

    /**
     * 任务组合查询唯一条件
     * @param serverCode
     * @param enterpriseCode
     * @param operaCode
     * @param reportServerVersion
     * @return
     */
    public ReportTask findByByUniqueCondition(String serverCode, String enterpriseCode, String operaCode,String reportServerVersion) {

        return mongoTemplate.findOne(Query.query(
                Criteria.where("serverCode").is(serverCode)
                        .and("enterpriseCode").is(enterpriseCode)
                        .and("operaCode").is(operaCode)
                        .and("reportServerVersion").is(reportServerVersion)
        ), ReportTask.class, MongoDocName.REPORT_TASK);
    }


}
