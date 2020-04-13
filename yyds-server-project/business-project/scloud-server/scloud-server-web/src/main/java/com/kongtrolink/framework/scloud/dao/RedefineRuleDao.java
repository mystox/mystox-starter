package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.RedefineRule;
import com.kongtrolink.framework.scloud.query.RedefineRuleQuery;
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
 * @Date: 2020/4/13 14:06
 * @Description:
 */
@Repository
public class RedefineRuleDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = CollectionSuffix.REDEDINE_RULE;

    public boolean add(String uniqueCode, RedefineRule redefineRule) {
        mongoTemplate.save(redefineRule, uniqueCode + table);
        return !StringUtil.isNUll(redefineRule.getId());
    }

    public boolean delete(String uniqueCode, String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, uniqueCode + table);
        return remove.getN()>0 ? true : false;
    }

    public boolean update(String uniqueCode, RedefineRule redefineRule) {
        if(delete(uniqueCode, redefineRule.getId())){
            if(add(uniqueCode, redefineRule)){
                return true;
            }
        }
        return false;
    }

    public List<RedefineRule> list(String uniqueCode, RedefineRuleQuery ruleQuery) {
        Criteria criteria = baseCriteria(ruleQuery);
        Query query = Query.query(criteria);
        int currentPage = ruleQuery.getCurrentPage();
        int pageSize = ruleQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);
        return mongoTemplate.find(query, RedefineRule.class, uniqueCode + table);
    }

    public int count(String uniqueCode, RedefineRuleQuery ruleQuery) {
        Criteria criteria = baseCriteria(ruleQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + table);
    }

    Criteria baseCriteria(RedefineRuleQuery ruleQuery){
        Criteria criteria = new Criteria();
        String id = ruleQuery.getId();
        if(!StringUtil.isNUll(id)){
            criteria.and("_id").is(id);
        }
        String name = ruleQuery.getName();
        if(!StringUtil.isNUll(name)){
            name = MongoRegexUtil.escapeExprSpecialWord(name);
            criteria.and("name").regex(".*?" + name + ".*?");
        }
        return criteria;
    }

    public RedefineRule getByName(String uniqueCode, String name) {
        Criteria criteria = Criteria.where("name").is(name);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, RedefineRule.class, uniqueCode + table);
    }

    public boolean updateState(String uniqueCode, RedefineRuleQuery redefineRuleQuery) {
        Criteria criteria = Criteria.where("_id").is(redefineRuleQuery.getId());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("enabled", redefineRuleQuery.getEnabled());
        update.set("updateTime", redefineRuleQuery.getUpdateTime());
        WriteResult result = mongoTemplate.updateFirst(query, update, uniqueCode + table);
        return result.getN()>0 ? true : false;
    }

    public RedefineRule matchRule(String uniqueCode, String siteCode, String deviceType, String cntbId) {
        Criteria criteria = Criteria.where("siteCodeList").is(siteCode);
        criteria.and("deviceType").is(deviceType);
        criteria.and("cntbIdList").is(cntbId);
        criteria.and("enabled").is(true);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return mongoTemplate.findOne(query, RedefineRule.class, uniqueCode + table);
    }
}
