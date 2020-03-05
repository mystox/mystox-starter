package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.ShieldAlarm;
import com.kongtrolink.framework.scloud.query.ShieldAlarmQuery;
import com.kongtrolink.framework.scloud.query.ShieldRuleQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.BulkWriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 09:22
 * @Description:
 */
@Repository
public class ShieldAlarmDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = "_alarm_focus";

    public boolean add(String uniqueCode, ShieldAlarm shieldAlarm) {
        mongoTemplate.save(shieldAlarm, uniqueCode + table);
        return StringUtil.isNUll(shieldAlarm.getId()) ? false : true;
    }

    public int add(String uniqueCode, List<ShieldAlarm> shieldAlarmList) {
        BulkOperations operations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, uniqueCode + table);
        for(ShieldAlarm shieldAlarm : shieldAlarmList){
            operations.insert(shieldAlarm);
        }
        BulkWriteResult execute = operations.execute();
        return execute.getInsertedCount();
    }

    public List<ShieldAlarm> list(String uniqueCode, ShieldAlarmQuery shieldAlarmQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, shieldAlarmQuery);
        Query query = Query.query(criteria);
        query.skip( (shieldAlarmQuery.getCurrentPage()-1) * shieldAlarmQuery.getPageSize()).limit(shieldAlarmQuery.getPageSize());
        query.with(new Sort(Sort.Direction.DESC, "treport"));
        return mongoTemplate.find(query, ShieldAlarm.class, uniqueCode + table);
    }

    Criteria baseCriteria(Criteria criteria, ShieldAlarmQuery shieldAlarmQuery){
        if(!StringUtil.isNUll(shieldAlarmQuery.getRuleId())){
            criteria.and("ruleId").is(shieldAlarmQuery.getRuleId());
        }
        String ruleName = shieldAlarmQuery.getRuleName();
        if(!StringUtil.isNUll(ruleName)){
            ruleName = MongoRegexUtil.escapeExprSpecialWord(ruleName);
            criteria.and("ruleName").regex(".*?" + ruleName + ".*?");
        }
        return criteria;
    }

    public int count(String uniqueCode, ShieldAlarmQuery shieldAlarmQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, shieldAlarmQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + table);
    }
}
