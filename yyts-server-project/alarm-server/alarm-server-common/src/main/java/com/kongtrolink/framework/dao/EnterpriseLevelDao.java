package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
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
 * @Date: 2019/9/20 10:43
 * @Description:
 */
@Repository
public class EnterpriseLevelDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.ENTERPRISE_LEVEL;

    public void add(EnterpriseLevel enterpriseLevel) {
        mongoTemplate.save(enterpriseLevel, table);
    }

    public boolean delete(String enterpriseLevelId) {
        Criteria criteria = Criteria.where("_id").is(enterpriseLevelId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    /**
     * @auther: liudd
     * @date: 2019/9/20 11:01
     * 功能描述:只允许修改等级和颜色
     */
    public boolean update(EnterpriseLevel enterpriseLevel) {
        Criteria criteria = Criteria.where("_id").is(enterpriseLevel.getId());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("level", enterpriseLevel.getLevel());
        update.set("color", enterpriseLevel.getColor());
        update.set("updateTime", enterpriseLevel.getUpdateTime());
        update.set("defaultLevel", enterpriseLevel.getDefaultLevel());
        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;
    }

    public List<EnterpriseLevel> list(EnterpriseLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, levelQuery);
        Query query = Query.query(criteria);
        int currentPage = levelQuery.getCurrentPage();
        int pageSize = levelQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);
        return mongoTemplate.find(query, EnterpriseLevel.class, table);
    }

    public int count(EnterpriseLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, levelQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, table);
    }

    Criteria baseCriteria(Criteria criteria, EnterpriseLevelQuery levelQuery){
        String id = levelQuery.getId();
        if(!StringUtil.isNUll(id)){
            criteria.and("_id").is(id);
        }
        String enterpriseCode = levelQuery.getEnterpriseCode();
        if(!StringUtil.isNUll(enterpriseCode)){
            criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String serverCode = levelQuery.getServerCode();
        if(!StringUtil.isNUll(serverCode)){
            criteria.and("serverCode").is(serverCode);
        }
        String level = levelQuery.getLevel();
        if(!StringUtil.isNUll(level)){
            criteria.and("level").is(level);
        }
        String color = levelQuery.getColor();
        if(!StringUtil.isNUll(color)) {
            criteria.and("color").is(color);
        }
        String defaultLevel = levelQuery.getDefaultLevel();
        if(!StringUtil.isNUll(defaultLevel)){
            criteria.and("defaultLevel").is(defaultLevel);
        }
        return criteria;
    }

    /**
     * @auther: liudd
     * @date: 2019/9/20 11:15
     * 功能描述:按条件获取一条数据
     */
    public EnterpriseLevel getOne(EnterpriseLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, levelQuery);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return mongoTemplate.findOne(query, EnterpriseLevel.class, table);
    }

    public boolean updateDefault(EnterpriseLevelQuery enterpriseLevelQuery, String defaultStr){
        Criteria criteria = new Criteria();
        baseCriteria(criteria, enterpriseLevelQuery);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("defaultLevel", defaultStr);
        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;
    }

    /**
     * @auther: liudd
     * @date: 2019/9/25 15:02
     * 功能描述:获取最大告警等级。如果指明告警等级，则获取小于该
     */
    public EnterpriseLevel getMaxLevel(EnterpriseLevelQuery enterpriseLevelQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, enterpriseLevelQuery);
        String level = enterpriseLevelQuery.getLevel();
        if(!StringUtil.isNUll(level)){
            criteria.and("level").lte(level);
        }
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "level"));
        return mongoTemplate.findOne(query, EnterpriseLevel.class, table);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/12 14:33
     * 功能描述:匹配告警
     */
    public EnterpriseLevel matchLevel(String enterpriseCode, String serverCode, String level){
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        criteria.and("level").is(level);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "level"));
        return mongoTemplate.findOne(query, EnterpriseLevel.class, table);
    }

}
