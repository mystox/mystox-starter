package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.*;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.query.AlarmCycleQuery;
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
 * @Date: 2019/9/21 11:02
 * @Description:
 */
@Repository
public class AlarmCycleDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.ALARM_CYCLE;

    public void save(AlarmCycle alarmCycle) {
        mongoTemplate.save(alarmCycle, table);
    }

    public boolean delete(String alarmCycleCycleId) {
        Criteria criteria = Criteria.where("_id").is(alarmCycleCycleId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    public boolean update(AlarmCycle alarmCycle) {
        Criteria criteria = Criteria.where("_id").is(alarmCycle.getId());
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        boolean result = remove.getN() > 0 ? true : false;
        if(result){
            mongoTemplate.save(alarmCycle, table);
        }
        return result;
    }

    public AlarmCycle get(String alarmCycleId) {
        Criteria criteria = Criteria.where("_id").is(alarmCycleId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, AlarmCycle.class, table);
    }

    public List<AlarmCycle> list(AlarmCycleQuery cycleQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, cycleQuery);
        Query query = Query.query(criteria);
        int currentPage = cycleQuery.getCurrentPage();
        int pageSize = cycleQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);

        return mongoTemplate.find(query, AlarmCycle.class, table);
    }

    public int count(AlarmCycleQuery cycleQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, cycleQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, table);
    }

    Criteria baseCriteria(Criteria criteria, AlarmCycleQuery cycleQuery){
        String id = cycleQuery.getId();
        if(!StringUtil.isNUll(id)){
            criteria.and("_id").is(id);
        }
        String name = cycleQuery.getName();
        if(!StringUtil.isNUll(name)){
            name = MongoUtil.escapeExprSpecialWord(name);
            criteria.and("name").regex(".*?" + name + ".*?");
        }
        String enterpriseCode = cycleQuery.getEnterpriseCode();
        if(!StringUtil.isNUll(enterpriseCode)){
            criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String enterpriseName = cycleQuery.getEnterpriseName();
        if(!StringUtil.isNUll(enterpriseName)){
            enterpriseName = MongoUtil.escapeExprSpecialWord(enterpriseName);
            criteria.and("enterpriseName").regex(".*?" + enterpriseName + ".*?");
        }
        String serverCode = cycleQuery.getServerCode();
        if(!StringUtil.isNUll(serverCode)){
            criteria.and("serverCode").is(serverCode);
        }
        String serverName = cycleQuery.getServerName();
        if(!StringUtil.isNUll(serverName)){
            serverName = MongoUtil.escapeExprSpecialWord(serverName);
            criteria.and("serverName").regex(".*?" + serverName + ".*?");
        }
        String operatorName = cycleQuery.getOperatorName();
        if(!StringUtil.isNUll(operatorName)){
            operatorName = MongoUtil.escapeExprSpecialWord(operatorName);
            criteria.and("operator.name").regex(".*?" + operatorName + ".*?");
        }
        Date beginTime = cycleQuery.getBeginTime();
        Date endTime = cycleQuery.getEndTime();
        if(null != beginTime && null == endTime){
            criteria.and("updateTime").gte(beginTime);
        }else if(null != beginTime && null != endTime){
            criteria.and("updateTime").gte(beginTime).lte(endTime);
        }else if(null == beginTime && null != endTime){
            criteria.and("updateTime").lte(endTime);
        }
        return criteria;
    }

    public AlarmCycle getOne(AlarmCycleQuery alarmCycleQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, alarmCycleQuery);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, AlarmCycle.class, table);
    }

    public boolean updateState(String enterpriseCode, String serverCode, String id,
                               String state, Date curTime, FacadeView operator) {
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        criteria.and("_id").is(id);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", state);
        update.set("updateTime", curTime);
        update.set("operator", operator);
        WriteResult result = mongoTemplate.updateMulti(query, update, table);
        return result.getN()>0 ? true : false;
    }

    public AlarmCycle getLastUpdateOne(AlarmCycleQuery alarmCycleQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, alarmCycleQuery);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return mongoTemplate.findOne(query, AlarmCycle.class, table);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/14 18:15
     * 功能描述:根据企业服务编码，获取自定义周期规则
     */
    public List<AlarmCycle> getCycleList(List<String> enterpriseServerList){
        Criteria criteria = new Criteria();
        Criteria enterCri = Criteria.where("enterpriseServer").in(enterpriseServerList);
        //获取默认周期规则
        Criteria systemCri = Criteria.where("enterpriseCode").exists(false);
        criteria.orOperator(enterCri, systemCri);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, AlarmCycle.class, table);
    }
    /**
     * @param enterpriseCode
     * @param serverCode
     * @auther: liudd
     * @date: 2019/10/18 15:41
     * 功能描述:禁用以前的规则
     */
    public boolean forbitBefor(String enterpriseCode, String serverCode, Date curTime, FacadeView operator) {
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        criteria.and("state").is(Contant.USEING);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", Contant.FORBIT);
        update.set("updateTime", curTime);
        update.set("operator", operator);
        mongoTemplate.updateMulti(query, update, table);
        return true;
    }

    /**
     * @auther: liudd
     * @date: 2019/12/27 11:11
     * 功能描述:系统默认告警周期，没有企业识别码和服务编码，用于初始化其他企业默认告警周期
     */
    public AlarmCycle getSystemCycle(String enterpriseCode, String serverCode) {
        Criteria criteria = Criteria.where("cycleType").is(Contant.SYSTEM);
        if(StringUtil.isNUll(enterpriseCode)){
            criteria.and("enterpriseCode").exists(false);
        }else {
            criteria.and("enterpriseCode").is(enterpriseCode);
        }
        if(StringUtil.isNUll(serverCode)){
            criteria.and("serverCode").exists(false);
        }else{
            criteria.and("serverCode").is(serverCode);
        }
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, AlarmCycle.class, table);
    }


    /**
     * @param enterpriseServer
     * @param serverCode
     * @param name
     * @auther: liudd
     * @date: 2019/12/27 14:55
     * 功能描述:根据名称获取
     */
    public AlarmCycle getByName(String enterpriseServer, String serverCode, String name) {
        name = MongoUtil.escapeExprSpecialWord(name);
        Criteria criteria = Criteria.where("enterpriseServer").is(enterpriseServer);
        criteria.and("serverCode").is(serverCode);
        criteria.and("name").is(name);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, AlarmCycle.class, table);
    }
}
