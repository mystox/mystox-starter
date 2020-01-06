package com.kongtrolink.framework.reports.dao;

import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.ReportConfigRecord;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2019/12/20 9:46
 * \* Description:
 * \
 */
@Component
public class ReportConfigRecordDao extends MongoBaseDao {

    public ReportConfigRecord findByUniqueCondition(String enterpriseCode, String serverCode, String funcPrivCode, String operaCode) {
        return mongoTemplate.findOne(Query.query(Criteria.where("enterpriseCode").is(enterpriseCode)
                .and("serverCode").is(serverCode)
                .and("funcPrivCode").is(funcPrivCode)
                .and("operaCode").is(operaCode)
        ), ReportConfigRecord.class, MongoDocName.REPORT_CONFIG_RECORD);
    }

    public ReportConfigRecord findByReportTaskIdAndFuncPrivCode(String reportTaskId, String funPrivCode) {
        return mongoTemplate.findOne(Query.query(Criteria.where("reportTaskId").is(reportTaskId).and("funcPrivCode").is(funPrivCode)), ReportConfigRecord.class, MongoDocName.REPORT_CONFIG_RECORD);

    }

    public void save(ReportConfigRecord reportConfigRecord) {
        mongoTemplate.save(reportConfigRecord,MongoDocName.REPORT_CONFIG_RECORD);
    }

    public List<ReportConfigRecord> findByServerCodeAndEnterpriseCode(String serverCode, String enterpriseCode) {
        return mongoTemplate.find(Query.query(Criteria.where("serverCode").is(serverCode).and("enterpriseCode").is(enterpriseCode)), ReportConfigRecord.class, MongoDocName.REPORT_CONFIG_RECORD);

    }

    public List<ReportConfigRecord> findByServerCodeAndEnterpriseCodeAndFuncPrivCode(String serverCode, String enterpriseCode, String funcPrivCode) {
        return mongoTemplate.find(Query.query(Criteria.where("serverCode").is(serverCode).and("enterpriseCode").is(enterpriseCode).and("funcPrivCode").is(funcPrivCode)), ReportConfigRecord.class, MongoDocName.REPORT_CONFIG_RECORD);

    }

    public List<ReportConfigRecord> removeByServerCodeAndEnterpriseCodeAndFuncPrivCode(String serverCode, String enterpriseCode, String funcPrivCode) {
        return mongoTemplate.findAllAndRemove(Query.query(Criteria.where("serverCode").is(serverCode).and("enterpriseCode").is(enterpriseCode).and("funcPrivCode").is(funcPrivCode)), ReportConfigRecord.class, MongoDocName.REPORT_CONFIG_RECORD);
    }
}