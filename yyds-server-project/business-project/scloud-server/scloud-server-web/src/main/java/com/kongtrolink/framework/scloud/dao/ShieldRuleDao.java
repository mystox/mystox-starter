package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.ShieldRule;
import com.kongtrolink.framework.scloud.query.ShieldRuleQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/4 14:51
 * @Description:
 */
@Repository
public class ShieldRuleDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = "_shield_rule";

    public boolean add(String uniqueCode, ShieldRule shieldRule) {
        mongoTemplate.save(shieldRule, uniqueCode + table);
        return StringUtil.isNUll(shieldRule.getId()) ? false : true ;
    }

    public boolean delete(String uniqueCode, String shieldRuleId) {
        Criteria criteria = Criteria.where("_id").is(shieldRuleId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, uniqueCode + table);
        return remove.getN()>0 ? true : false;
    }

    public boolean updateState(String uniqueCode, String shieldId, Boolean state) {
        Criteria criteria = Criteria.where("_id").is(shieldId);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("enabled", state);
        WriteResult result = mongoTemplate.updateFirst(query, update, uniqueCode + table);
        return result.getN()>0 ? true : false;
    }

    public List<ShieldRule> list(String uniqueCode, ShieldRuleQuery shieldRuleQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, shieldRuleQuery);
        Query query = Query.query(criteria);
        int currentPage = shieldRuleQuery.getCurrentPage();
        int pageSize = shieldRuleQuery.getPageSize();
        query.skip( (currentPage-1) * pageSize).limit(pageSize);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return mongoTemplate.find(query, ShieldRule.class, uniqueCode + table);
    }

    private Criteria baseCriteria(Criteria criteria, ShieldRuleQuery ruleQuery){
        String name = ruleQuery.getName();
        if(!StringUtil.isNUll(name)){
            name = MongoRegexUtil.escapeExprSpecialWord(name);
            criteria.and("name").regex(".*?" + name + ".*?");
        }
        String startTime = ruleQuery.getStartTime();
        String endTime = ruleQuery.getEndTime();
        if(null != startTime && null == endTime){
            criteria.and("updateTime").gte(startTime);
        }else if(null != startTime && null != endTime){
            criteria.and("updateTime").gte(startTime).lte(endTime);
        }else if(null == startTime && null != endTime){
            criteria.and("updateTime").lte(endTime);
        }
        String creatorName = ruleQuery.getCreatorName();
        if(!StringUtil.isNUll(creatorName)){
            creatorName = MongoRegexUtil.escapeExprSpecialWord(creatorName);
            criteria.and("creator.name").regex(".*?" + creatorName + ".*?");
        }

        return criteria;
    }

    public int count(String uniqueCode, ShieldRuleQuery ruleQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, ruleQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + table);
    }

    public ShieldRule get(String uniqueCode, String ruleId) {
        Criteria criteria = Criteria.where("_id").is(ruleId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, ShieldRule.class, uniqueCode + table);
    }

    public List<ShieldRule> getEnables(String uniqueCode){
        Criteria criteria = Criteria.where("enabled").is(true);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, ShieldRule.class, uniqueCode + table);
    }
}
