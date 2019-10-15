package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.StringUtil;
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
//        Query query = Query.query(criteria);
//        Update update = new Update();
//        update.set("state", Contant.DELETED);
//        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN()>0 ? true : false;
    }

    public boolean update(AlarmCycle alarmCycle) {
        Criteria criteria = Criteria.where("_id").is(alarmCycle.getId());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("diffTime", alarmCycle.getDiffTime());
        update.set("updateTime", alarmCycle.getUpdateTime());
        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;
    }

    public AlarmCycle get(String alarmCycleId) {
        Criteria criteria = Criteria.where("_id");
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
        String uniqueCode = cycleQuery.getUniqueCode();
        if(!StringUtil.isNUll(uniqueCode)){
            criteria.and("uniqueCode").is(uniqueCode);
        }
        String service = cycleQuery.getService();
        if(!StringUtil.isNUll(service)){
            criteria.and("service").is(service);
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

    public boolean updateState(AlarmCycleQuery cycleQuery) {
        String state = cycleQuery.getState();
        Criteria criteria = new Criteria();
        baseCriteria(criteria, cycleQuery);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", state);
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
}
