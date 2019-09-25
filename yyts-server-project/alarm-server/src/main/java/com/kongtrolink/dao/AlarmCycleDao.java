package com.kongtrolink.dao;

import com.kongtrolink.base.Contant;
import com.kongtrolink.base.MongTable;
import com.kongtrolink.base.StringUtil;
import com.kongtrolink.enttiy.AlarmCycle;
import com.kongtrolink.query.AlarmCycleQuery;
import com.kongtrolink.query.AlarmLevelQuery;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
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
        Update update = new Update();
        update.set("state", Contant.DELETED);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, table);
        return updateResult.getModifiedCount()>0 ? true : false;
    }

    public boolean update(AlarmCycle alarmCycle) {
        Criteria criteria = Criteria.where("_id").is(alarmCycle.getId());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("diffTime", alarmCycle.getDiffTime());
        update.set("updateTime", alarmCycle.getUpdateTime());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, table);
        return updateResult.getModifiedCount()>0 ? true : false;
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
        String uniqueCode = cycleQuery.getUniqueCode();
        if(!StringUtil.isNUll(uniqueCode)){
            criteria.and("uniqueCode").is(uniqueCode);
        }
        String service = cycleQuery.getService();
        if(!StringUtil.isNUll(service)){
            criteria.and("service").is(service);
        }
//        String propertyStr = cycleQuery.getPropertyStr();
//        if(!StringUtil.isNUll(propertyStr)){
//            if(Contant.TREPORT.equals(propertyStr)){
//                criteria.and(propertyStr).gte(cycleQuery.getDiffTime());
//            }else if(Contant.TRECOVER.equals(propertyStr)){
//                criteria.andOperator(Criteria.where(propertyStr).exists(true), Criteria.where(propertyStr).gte(cycleQuery.getDiffTime()));
//            }
//        }
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

    public boolean updateState(AlarmCycleQuery cycleQuery) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, cycleQuery);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", cycleQuery.getState());
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, table);
        return updateResult.getModifiedCount()>0 ? true : false;
    }
}