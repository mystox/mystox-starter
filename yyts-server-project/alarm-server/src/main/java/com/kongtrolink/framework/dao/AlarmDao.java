package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.query.AlarmQuery;
import com.mongodb.WriteResult;
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
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:41
 * @Description:
 */
@Repository
public class AlarmDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public void save(Alarm alarm, String table) {
        mongoTemplate.save(alarm, table);
    }

    public boolean delete(String alarmId, String table) {
        Criteria criteria = Criteria.where("_id").is(alarmId);
        Query query = Query.query(criteria);
        WriteResult result = mongoTemplate.remove(query, table);
        return result.getN()>0 ? true : false;
    }

    public boolean update(Alarm alarm, String table) {
        Criteria criteria = Criteria.where("_id").is(alarm.getId());
        Query query = Query.query(criteria);
        Update update = new Update();

        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;
    }

    public List<Alarm> list(AlarmQuery alarmQuery, String table) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, alarmQuery);
        Query query = Query.query(criteria);
        int currentPage = alarmQuery.getCurrentPage();
        int pageSize = alarmQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);
        return mongoTemplate.find(query, Alarm.class, table);
    }

    public int count(AlarmQuery alarmQuery, String table) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, alarmQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, table);
    }

    Criteria baseCriteria(Criteria criteria, AlarmQuery alarmQuery){
        String id = alarmQuery.getId();
        if(!StringUtil.isNUll(id)){
            criteria.and("_id").is(id);
        }
        List<String> alarmIdList = alarmQuery.getAlarmIdList();
        if(null != alarmIdList && alarmIdList.size()>0){
            criteria.and("_id").in(alarmIdList);
        }
        List<String> levelList = alarmQuery.getLevelList();
        if(null != levelList && levelList.size()>0){
            criteria.and("level").in(levelList);
        }
        String state = alarmQuery.getState();
        if(!StringUtil.isNUll(state)){
            criteria.and("state").is(state);
        }
        Date beginTime = alarmQuery.getBeginTime();
        Date endTime = alarmQuery.getEndTime();
        if(null != beginTime && null == endTime){
            criteria.and("tReport").gte(beginTime);
        }else if(null != beginTime && null != endTime){
            criteria.and("tReport").gte(beginTime).lte(endTime);
        }else if(null == beginTime && null != endTime){
            criteria.and("tReport").lte(endTime);
        }
        //设备属性查询
        Map<String, String> deviceInfo = alarmQuery.getDeviceInfo();
        if(null != deviceInfo){
            for(String key : deviceInfo.keySet()){
                String value = deviceInfo.get(key);
                criteria.and(key).is(value);
            }
        }
        return criteria;
    }

    public int deleteList(AlarmQuery alarmQuery, String table){
        Criteria criteria = new Criteria();
        baseCriteria(criteria, alarmQuery);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return (int)remove.getN();
    }

    public void addList(List<Alarm> alarmList, String table) {
        mongoTemplate.save(alarmList, table);
    }

    /**
     * @param alarm
     * @param table
     * @auther: liudd
     * @date: 2019/9/24 11:10
     * 功能描述:修改告警属性，包括附属属性
     */
    public boolean updateProperties(Alarm alarm, String table) {
        Map<String, String> auxilaryMap = alarm.getAuxilaryMap();
        if(null != auxilaryMap){
            Criteria criteria = Criteria.where("_id").is(alarm.getId());
            Query query = Query.query(criteria);
            Update update = new Update();
            for(String key : auxilaryMap.keySet()){
                update.set("key", auxilaryMap.get(key));
            }
            WriteResult result = mongoTemplate.updateFirst(query, update, table);
            return result.getN()>0 ? true : false;
        }
        return true;
    }
}
