package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.MongoUtil;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.InformRule;
import com.kongtrolink.framework.query.InformRuleQuery;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 09:45
 * @Description:
 */
@Repository
public class InformRuleDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.INFORM_RULE;

    public boolean save(InformRule deliver) {
        mongoTemplate.save(deliver, table);
        return !StringUtil.isNUll(deliver.get_id());
    }

    public boolean delete(String deliverId) {
        Criteria criteria = Criteria.where("_id").is(deliverId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    public boolean update(InformRule deliver) {
        boolean result = false;
        boolean delete = delete(deliver.get_id());
        if(delete){
            result = save(deliver);
        }
        return result;
    }

    public InformRule get(String ruleId) {
        Criteria criteria = Criteria.where("_id").is(ruleId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, InformRule.class, table);
    }

    public List<InformRule> list(InformRuleQuery deliverQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, deliverQuery);
        Query query = Query.query(criteria);
        int currentPage = deliverQuery.getCurrentPage();
        int pageSize = deliverQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);
        return mongoTemplate.find(query, InformRule.class, table);
    }

    public int count(InformRuleQuery deliverQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, deliverQuery);
        Query query = Query.query(criteria);
        return  (int)mongoTemplate.count(query, table);
    }

    Criteria baseCriteria(Criteria criteria, InformRuleQuery deliverQuery){
        String id = deliverQuery.get_id();
        if(!StringUtil.isNUll(id)){
            criteria.and("_id").is(id);
        }
        String enterpirseName = deliverQuery.getEnterpirseName();
        if(!StringUtil.isNUll(enterpirseName)){
            enterpirseName = MongoUtil.escapeExprSpecialWord(enterpirseName);
            criteria.and("enterprise.name").regex(".*?" + enterpirseName + ".*?");
        }
        String serviceName = deliverQuery.getServiceName();
        if(!StringUtil.isNUll(serviceName)){
            serviceName = MongoUtil.escapeExprSpecialWord(serviceName);
            criteria.and("service.name").regex(".*?" + serviceName + ".*?");
        }
        String status = deliverQuery.getStatus();
        if(!StringUtil.isNUll(status)){
            criteria.and("status").is(status);
        }

        return criteria;
    }

    public InformRule getOne(InformRuleQuery deliverQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, deliverQuery);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, InformRule.class, table);
    }

    /**
     * @param name
     * @auther: liudd
     * @date: 2018/7/3 18:36
     * 功能描述:根据规则名称获取规则，主要用于判断名称唯一性
     */
    public InformRule getByName(String name) {
        Criteria criteria = Criteria.where("name").is(name);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, InformRule.class, table);
    }

    public boolean updateStatus(String ruleId, String status) {
        Criteria criteria = Criteria.where("_id").is(ruleId);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("status", status);
        update.set("updateTime", new Date());
        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;
    }
}
