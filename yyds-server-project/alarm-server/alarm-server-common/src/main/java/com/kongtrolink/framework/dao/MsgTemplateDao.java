package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.MongoUtil;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.MsgTemplate;
import com.kongtrolink.framework.query.MsgTemplateQuery;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/23 14:33
 * @Description:
 */
@Repository
public class MsgTemplateDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.MSG_TEMPLATE;

    public boolean save(MsgTemplate msgTemplate) {
        mongoTemplate.save(msgTemplate, table);
        if(!StringUtil.isNUll(msgTemplate.get_id())){
            return true;
        }
        return false;
    }

    public boolean delete(String msgTemplateId) {
        Criteria criteria = Criteria.where("_id").is(msgTemplateId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    public boolean update(MsgTemplate msgTemplate) {
        Criteria criteria = Criteria.where("_id").is(msgTemplate.get_id());
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        boolean result = remove.getN() > 0 ? true : false;
        if(result){
            mongoTemplate.save(msgTemplate, table);
        }
        return result;
    }

    public MsgTemplate get(String msgTemplateId) {
        Criteria criteria = Criteria.where("_id").is(msgTemplateId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, MsgTemplate.class, table);
    }

    public List<MsgTemplate> list(MsgTemplateQuery msgTemplateQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, msgTemplateQuery);
        Query query = Query.query(criteria);
        int currentPage = msgTemplateQuery.getCurrentPage();
        int pageSize = msgTemplateQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);
        query.with(new Sort(Sort.Direction.ASC, "enterpriseCode"));
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return mongoTemplate.find(query, MsgTemplate.class, table);
    }

    public int count(MsgTemplateQuery msgTemplateQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, msgTemplateQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, table);
    }

    private Criteria baseCriteria(Criteria criteria , MsgTemplateQuery msgTemplateQuery){

        Criteria enterpriseCri = new Criteria();

        String id = msgTemplateQuery.get_id();
        if(!StringUtil.isNUll(id)){
            enterpriseCri.and("_id").is(id);
        }
        String name = msgTemplateQuery.getName();
        if(!StringUtil.isNUll(name)){
            name = MongoUtil.escapeExprSpecialWord(name);
            enterpriseCri.and("name").regex(".*?" + name + ".*?");
        }
        String enterpriseCode = msgTemplateQuery.getEnterpriseCode();
        if(!StringUtil.isNUll(enterpriseCode)){
            enterpriseCri.and("enterpriseCode").is(enterpriseCode);
        }
        String enterpriseName = msgTemplateQuery.getEnterpriseName();
        if(!StringUtil.isNUll(enterpriseName)){
            enterpriseName = MongoUtil.escapeExprSpecialWord(enterpriseName);
            enterpriseCri.and("enterpriseName").regex(".*?" + enterpriseName + ".*?");
        }
        String serverCode = msgTemplateQuery.getServerCode();
        if(!StringUtil.isNUll(serverCode)){
            enterpriseCri.and("serverCode").is(serverCode);
        }
        String serverName = msgTemplateQuery.getServerName();
        if(!StringUtil.isNUll(serverName)){
            serverName = MongoUtil.escapeExprSpecialWord(serverName);
            enterpriseCri.and("serverName").regex(".*?" + serverName + ".*?");
        }
        String type = msgTemplateQuery.getType();
        if(!StringUtil.isNUll(type)){
            enterpriseCri.and("type").is(type);
        }
        //liuddtodo 可能需要根据一个编码，同时对上报告警，告警消除，离线的编码做模糊查询
        //兼容默认规则
        Criteria defalutCri = Criteria.where("enterpriseCode").exists(false);
        criteria.orOperator(enterpriseCri, defalutCri);
        return criteria;
    }

    public MsgTemplate getByName(String enterpriseCode, String serverCode, String name) {
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        criteria.and("name").is(name);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, MsgTemplate.class, table);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/26 16:03
     * 功能描述:获取系统默认模板
     */
    public MsgTemplate getSystemTemplate(String type){
        Criteria criteria = Criteria.where("templateType").is(Contant.SYSTEM);
        criteria.and("type").is(type);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, MsgTemplate.class, MongTable.MSG_TEMPLATE);
    }
}
