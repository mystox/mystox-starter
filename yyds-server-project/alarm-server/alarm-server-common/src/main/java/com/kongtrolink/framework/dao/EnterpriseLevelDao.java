package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.MongoUtil;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;

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

    public boolean deleteByCode(String code) {
        Criteria criteria = Criteria.where("code").is(code);
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
        WriteResult remove = mongoTemplate.remove(query, table);
        boolean result = remove.getN() > 0 ? true : false;
        if(result){
            mongoTemplate.save(enterpriseLevel, table);
        }
        return result;
    }

    public EnterpriseLevel get(String enterpriseLevelId) {
        Criteria criteria = Criteria.where("_id").is(enterpriseLevelId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, EnterpriseLevel.class, table);
    }

    public List<EnterpriseLevel> list(EnterpriseLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, levelQuery);
        Sort sort = new Sort(Sort.Direction.DESC, "code");
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),  //查询条件
                Aggregation.group("code", "updateTime"),
                Aggregation.sort(sort),
                Aggregation.skip((levelQuery.getCurrentPage() - 1) * levelQuery.getPageSize()),//跳到第几个开始
                Aggregation.limit(levelQuery.getPageSize())//查出多少个数据
        );
        AggregationResults<EnterpriseLevel> aggResult = mongoTemplate.aggregate(agg, table, EnterpriseLevel.class);
        List<EnterpriseLevel> mappedResults = aggResult.getMappedResults();
        List<String> codeList = new ArrayList<>();
        for(EnterpriseLevel enterpriseLevel : mappedResults){
            codeList.add(enterpriseLevel.getCode());
        }
        List<EnterpriseLevel> byCodes = getByCodes(codeList);
        List<EnterpriseLevel> resuList = new ArrayList<>();
        Map<String, EnterpriseLevel> codeEnterpriseMap = new HashMap<>();
        for(EnterpriseLevel enterpriseLevel : byCodes){
            EnterpriseLevel firEnter = codeEnterpriseMap.get(enterpriseLevel.getCode());
            if(null == firEnter){
                enterpriseLevel.setLevels(new ArrayList<>());
                enterpriseLevel.getLevels().add(enterpriseLevel.getLevel());
                enterpriseLevel.setLevelNames(new ArrayList<>());
                enterpriseLevel.getLevelNames().add(enterpriseLevel.getLevelName());
                enterpriseLevel.setColors(new ArrayList<>());
                enterpriseLevel.getColors().add(enterpriseLevel.getColor());
                codeEnterpriseMap.put(enterpriseLevel.getCode(), enterpriseLevel);
                resuList.add(enterpriseLevel);
                continue;
            }
            firEnter.getLevels().add(enterpriseLevel.getLevel());
            firEnter.getLevelNames().add(enterpriseLevel.getLevelName());
            firEnter.getColors().add(enterpriseLevel.getColor());
        }
        return resuList;
    }

    public int count(EnterpriseLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, levelQuery);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),  //查询条件
                Aggregation.group("code")
        );
        AggregationResults<EnterpriseLevel> aggResult = mongoTemplate.aggregate(agg, table, EnterpriseLevel.class);
        return aggResult.getMappedResults().size();
    }

    Criteria baseCriteria(Criteria criteria, EnterpriseLevelQuery levelQuery){
        String id = levelQuery.getId();
        if(!StringUtil.isNUll(id)){
            criteria.and("_id").is(id);
        }
        String name = levelQuery.getName();
        if(!StringUtil.isNUll(name)){
            name = MongoUtil.escapeExprSpecialWord(name);
            criteria.and("name").regex(".*?" + name + ".*?");
        }
        String enterpriseCode = levelQuery.getEnterpriseCode();
        if(!StringUtil.isNUll(enterpriseCode)){
            criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String enterpriseName = levelQuery.getEnterpriseName();
        if(!StringUtil.isNUll(enterpriseName)){
            enterpriseName = MongoUtil.escapeExprSpecialWord(enterpriseName);
            criteria.and("enterpriseName").regex(".*?" + enterpriseName + ".*?");
        }
        String serverCode = levelQuery.getServerCode();
        if(!StringUtil.isNUll(serverCode)){
            criteria.and("serverCode").is(serverCode);
        }
        String serverName = levelQuery.getServerName();
        if(!StringUtil.isNUll(serverName)){
            serverName = MongoUtil.escapeExprSpecialWord(serverName);
            criteria.and("serverName").regex(".*?" + serverName + ".*?");
        }
        String defaultLevel = levelQuery.getDefaultLevel();
        if(!StringUtil.isNUll(defaultLevel)){
            criteria.and("defaultLevel").is(defaultLevel);
        }
        String operatorName = levelQuery.getOperatorName();
        if(!StringUtil.isNUll(operatorName)){
            operatorName = MongoUtil.escapeExprSpecialWord(operatorName);
            criteria.and("operator.name").regex(".*?" + operatorName + ".*?");
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

    public boolean updateState(EnterpriseLevelQuery enterpriseLevelQuery) {
        String code = enterpriseLevelQuery.getCode();
        Criteria criteria = Criteria.where("code").is(code);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", enterpriseLevelQuery.getState());
        update.set("updateTime", new Date());
        WriteResult result = mongoTemplate.updateMulti(query, update, table);
        return result.getN()>0 ? true : false;
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/16 15:02
     * 功能描述:获取最后一次启用的企业告警
     */
    public List<EnterpriseLevel> getLastUse(String enterpriseCode, String serverCode) {
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        criteria.and("state").is(Contant.USEING);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "level"));
        List<EnterpriseLevel> enterpriseLevelList = mongoTemplate.find(query, EnterpriseLevel.class, table);
        if(enterpriseLevelList == null || enterpriseLevelList.size() == 0){
            enterpriseLevelList = getDefault();
        }
        return enterpriseLevelList;
    }

    public List<EnterpriseLevel> getDefault() {
        Criteria criteria = Criteria.where("defaultLevel").is(Contant.YES);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, EnterpriseLevel.class, table);
    }

    List<EnterpriseLevel> getByCodes(List<String> codeList){
        Criteria criteria = Criteria.where("code").in(codeList);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "level"));
        return mongoTemplate.find(query, EnterpriseLevel.class, table);
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/16 20:18
     * 功能描述:禁用原来的告警等级
     */
    public boolean forbitBefor(String enterpriseCode, String serverCode, Date updateTime) {
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", Contant.FORBIT);
        update.set("updateTime", updateTime);
        WriteResult result = mongoTemplate.updateMulti(query, update, table);
        return result.getN()>0 ? true : false;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/18 8:46
     * 功能描述:匹配企业告警最小值
     */
    public EnterpriseLevel matchLevel(String enterpriseCode, String serverCode, Integer level){
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        criteria.and("state").is(Contant.USEING);
        criteria.and("level").lte(level);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "level"));
        return mongoTemplate.findOne(query, EnterpriseLevel.class, table);
    }
}
