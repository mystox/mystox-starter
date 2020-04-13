package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.RedefineAlarm;
import com.kongtrolink.framework.scloud.query.RedefineAlarmQuery;
import com.kongtrolink.framework.scloud.query.RedefineRuleQuery;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 14:55
 * @Description:
 */
@Repository
public class RedefineAlarmDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = CollectionSuffix.REDEDINE_ALARM;

    public boolean add(String uniqueCode, RedefineAlarm redefineAlarm) {
        mongoTemplate.save(redefineAlarm, uniqueCode + table);
        return !StringUtil.isNUll(redefineAlarm.getId());
    }

    public List<RedefineAlarm> list(String uniqueCode, RedefineAlarmQuery redefineAlarmQuery) {
        Criteria criteria = baseCriteria(redefineAlarmQuery);
        Query query = Query.query(criteria);
        int currentPage = redefineAlarmQuery.getCurrentPage();
        int pageSize = redefineAlarmQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);
        return mongoTemplate.find(query, RedefineAlarm.class, uniqueCode + table);
    }

    public int count(String uniqueCode, RedefineAlarmQuery redefineAlarmQuery) {
        Criteria criteria = baseCriteria(redefineAlarmQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + table);
    }

    Criteria baseCriteria(RedefineAlarmQuery redefineAlarmQuery){
        Criteria criteria = new Criteria();
        String ruleId = redefineAlarmQuery.getRuleId();
        if(!StringUtil.isNUll(ruleId)){
            criteria.and("ruleId").is(ruleId);
        }

        return criteria;
    }
}
