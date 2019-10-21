package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.Contant;
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
        String name = deliverQuery.getName();
        if(!StringUtil.isNUll(name)){
            name = MongoUtil.escapeExprSpecialWord(name);
            criteria.and("name").regex(name);
        }
        String enterpriseCode = deliverQuery.getEnterpriseCode();
        if(!StringUtil.isNUll(enterpriseCode)){
            criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String enterpirseName = deliverQuery.getEnterpriseName();
        if(!StringUtil.isNUll(enterpirseName)){
            enterpirseName = MongoUtil.escapeExprSpecialWord(enterpirseName);
            criteria.and("enterpriseName").regex(".*?" + enterpirseName + ".*?");
        }
        String serverCode = deliverQuery.getServerCode();
        if(!StringUtil.isNUll(serverCode)){
            criteria.and("serverCode").is(serverCode);
        }
        String serverName = deliverQuery.getServerName();
        if(!StringUtil.isNUll(serverName)){
            serverName = MongoUtil.escapeExprSpecialWord(serverName);
            criteria.and("serverName").regex(".*?" + serverName + ".*?");
        }
        String status = deliverQuery.getStatus();
        if(!StringUtil.isNUll(status)){
            criteria.and("status").is(status);
        }
        String operatorName = deliverQuery.getOperatorName();
        if(!StringUtil.isNUll(operatorName)){
            operatorName = MongoUtil.escapeExprSpecialWord(operatorName);
            criteria.and("operator.name").regex(".*?" + operatorName + ".*?");
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



    public List<InformRule> matchInformMsg(String enterpriseCode, String serverCode, String level, Date treport){
        Criteria criteria = Criteria.where("msgEnable").is(Contant.USEING);
//        criteria.and("msgBeginTimeInt").lte(tReportInt);
//        criteria.and("msgEndTimeInt").gte(tReportInt);
//        criteria.and("msgDayList").is(week);
//        Criteria levelCriteria = Criteria.where("msgLevelList").is(level);
//        Criteria levelSignalCri = new Criteria();
//        levelSignalCri.orOperator(levelCriteria, signalCriteria);
//        criteria.andOperator(levelSignalCri);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, InformRule.class, table);
    }
}
