package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.entity.MongoDBTable;
import com.kongtrolink.framework.jsonType.JsonLoginParam;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * @author fengw
 * 注册参数数据库操作
 * 新建文件 2019-4-16 19:08:01
 */
@Component
public class LoginParamDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public JsonLoginParam getInfoByFsuId(String fsuId) {
        Criteria criteria = Criteria.where("fsuId").is(fsuId);
        return mongoTemplate.findOne(Query.query(criteria),
                JsonLoginParam.class, MongoDBTable.T_LOGIN_PARAM);
    }

    /**
     * 新增或更新指定FSU的注册参数信息
     * @param info 注册参数信息
     * @return 新增或更新结果
     */
    public boolean upsertInfoByFsuId(JsonLoginParam info) {
        boolean result;

        Criteria criteria = Criteria.where("fsuId").is(info.getFsuId());

        Update update = new Update();
        update.set("heartbeatInterval", info.getHeartbeatInterval());
        update.set("heartbeatTimeoutLimit", info.getHeartbeatTimeoutLimit());
        update.set("loginLimit", info.getLoginLimit());
        update.set("loginInterval", info.getLoginInterval());
        update.set("alarmReportLimit", info.getAlarmReportLimit());
        update.set("alarmReportInterval", info.getAlarmReportInterval());

        WriteResult writeResult = mongoTemplate.upsert(Query.query(criteria),
                update, MongoDBTable.T_LOGIN_PARAM);

        result = writeResult.getN() > 0;

        return result;
    }
}
