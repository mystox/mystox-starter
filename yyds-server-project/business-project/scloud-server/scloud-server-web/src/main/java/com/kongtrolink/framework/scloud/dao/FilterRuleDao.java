package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.FilterRule;
import com.kongtrolink.framework.scloud.query.FilterRuleQuery;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 15:01
 * @Description:
 */
@Repository
public class FilterRuleDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = "_filter_rule";

    public boolean add(String uniqueCode, FilterRule filterRule) {
        mongoTemplate.save(filterRule, uniqueCode + table);
        return StringUtil.isNUll(filterRule.getId()) ? false : true ;
    }

    public boolean delete(String uniqueCode, String filterRuleId) {
        Criteria criteria = Criteria.where("_id").is(filterRuleId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, uniqueCode + table);
        return remove.getN()>0 ? true : false;
    }

    public boolean update(String uniqueCode, FilterRule filterRule) {
        boolean result = delete(uniqueCode, filterRule.getId());
        if(result){
            result = add(uniqueCode, filterRule);
        }
        return result;
    }

    public List<FilterRule> list(String uniqueCode, FilterRuleQuery ruleQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, ruleQuery);
        Query query = Query.query(criteria);
        query.skip( (ruleQuery.getCurrentPage()-1) * ruleQuery.getPageSize()).limit(ruleQuery.getPageSize());
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return mongoTemplate.find(query, FilterRule.class, uniqueCode + table);
    }

    Criteria baseCriteria(Criteria criteria, FilterRuleQuery ruleQuery){
        if(!StringUtil.isNUll(ruleQuery.getCreatorId())){
            criteria.and("creator.strId").is(ruleQuery.getCreatorId());
        }
        return criteria;
    }

    public int count(String uniqeuCode, FilterRuleQuery ruleQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, ruleQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqeuCode + table);
    }

    /**
     * @auther: liudd
     * @date: 2020/3/5 15:33
     * 功能描述:禁用用户下已经启用的规则
     */
    public boolean unUse(String uniqueCode, FilterRuleQuery ruleQuery){
        Criteria criteria = Criteria.where("creator.strId").is(ruleQuery.getCreatorId());
        criteria.and("state").is(true);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", false);
        update.set("updateTime", ruleQuery.getUpdateTime());
        WriteResult result = mongoTemplate.updateFirst(query, update, uniqueCode + table);
        return result.getN()>0 ? true : false;
    }

    public boolean use(String uniqueCode, FilterRuleQuery ruleQuery) {
        Criteria criteria = Criteria.where("_id").is(ruleQuery.getId());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", ruleQuery.getState());
        update.set("updateTime", ruleQuery.getUpdateTime());
        WriteResult result = mongoTemplate.updateFirst(query, update, uniqueCode + table);
        return result.getN()>0 ? true : false;
    }

    /**
     * @param uniqueCode
     * @param creatorId
     * @auther: liudd
     * @date: 2020/3/5 15:49
     * 功能描述:获取用户正在启用的过滤规则
     */
    public FilterRule getUserInUse(String uniqueCode, String creatorId) {
        Criteria criteria = new Criteria();
        if(!StringUtil.isNUll(creatorId)){
            criteria.and("creator.strId").is(creatorId);
        }
        criteria.and("state").is(true);
        return mongoTemplate.findOne(Query.query(criteria), FilterRule.class, uniqueCode + table);
    }
}
