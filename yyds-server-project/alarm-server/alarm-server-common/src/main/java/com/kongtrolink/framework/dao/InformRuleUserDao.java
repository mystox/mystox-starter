package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.InformRule;
import com.kongtrolink.framework.enttiy.InformRuleUser;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 15:46
 * @Description:
 */
@Repository
public class InformRuleUserDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.INFORM_RULE_USER;

    public boolean save(InformRuleUser ruleUser) {
        mongoTemplate.save(ruleUser, table);
        if(!StringUtil.isNUll(ruleUser.get_id())){
            return true;
        }
        return false;
    }

    public boolean save(List<InformRuleUser> ruleUserList) {
        mongoTemplate.save(ruleUserList, table);
        return true;
    }

    public boolean delete(String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    public boolean deleteByRuleId(String ruleId) {
        Criteria criteria = Criteria.where("informRule.strId").is(ruleId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    public boolean deleteByUserIds(List<String> userIds) {
        Criteria criteria = Criteria.where("user.strId").in(userIds);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    public List<InformRuleUser> getByRuleId(String ruleId) {
        Criteria criteria = Criteria.where("informRule.strId").is(ruleId);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, InformRuleUser.class, table);
    }

    public List<InformRuleUser> getByRuleIdList(List<String> ruleIdList){
        Criteria criteria = Criteria.where("informRule.strId").in(ruleIdList);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, InformRuleUser.class, table);
    }
}
