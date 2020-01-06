package com.kongtrolink.framework.reports.dao;

import com.kongtrolink.framework.reports.entity.MongoDocName;
import com.kongtrolink.framework.reports.entity.ReportWebConfig;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/11/5, 10:52.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ReportWebConfigDao extends MongoBaseDao{


    public void save(ReportWebConfig reportWebConfig) {
        mongoTemplate.save(reportWebConfig, MongoDocName.REPORT_WEB_CONFIG);

    }
    public ReportWebConfig find(String serverCode, String enterpriseCode, String funcPrivCode) {
        return mongoTemplate.findOne(Query.query(
                Criteria.where("enterpriseCode").is(enterpriseCode)
                        .and("serverCode").is(serverCode).and("funcPrivCode").is(funcPrivCode)), ReportWebConfig.class, MongoDocName.REPORT_WEB_CONFIG);
    }

    public boolean exits(String serverCode, String enterpriseCode, String funcPrivCode) {
        return mongoTemplate.exists(Query.query(
                Criteria.where("enterpriseCode").is(enterpriseCode)
                        .and("serverCode").is(serverCode).and("funcPrivCode").is(funcPrivCode)), MongoDocName.REPORT_WEB_CONFIG);
    }


}
