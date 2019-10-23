package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongoUtil;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.query.AlarmQuery;
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
        String enterpriseCode = alarmQuery.getEnterpriseCode();
//        if(!StringUtil.isNUll(enterpriseCode)){
//            criteria.and("enterpriseCode").is(enterpriseCode);
//        }
//        String serverCode = alarmQuery.getServerCode();
//        if(!StringUtil.isNUll(serverCode)){
//            criteria.and("serverCode").is(serverCode);
//        }
        String deviceType = alarmQuery.getDeviceType();
        if(!StringUtil.isNUll(deviceType)){
            deviceType = MongoUtil.escapeExprSpecialWord(deviceType);
            criteria.and("deviceType").regex(".*?" + deviceType + ".*?");
        }
        String deviceModel = alarmQuery.getDeviceModel();
        if(!StringUtil.isNUll(deviceModel)){
            deviceModel = MongoUtil.escapeExprSpecialWord(deviceModel);
            criteria.and("deviceModel").regex(".*?" + deviceModel + ".*?");
        }
        String deviceId = alarmQuery.getDeviceId();
        if(!StringUtil.isNUll(deviceId)){
            criteria.and("deviceId").is(deviceId);
        }
        String signalId = alarmQuery.getSignalId();
        if(!StringUtil.isNUll(signalId)){
            criteria.and("signalId").is(signalId);
        }
        String serial = alarmQuery.getSerial();
        if(!StringUtil.isNUll(serial)){
            criteria.and("serial").is(serial);
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
            criteria.and("treport").gte(beginTime);
        }else if(null != beginTime && null != endTime){
            criteria.and("treport").gte(beginTime).lte(endTime);
        }else if(null == beginTime && null != endTime){
            criteria.and("treport").lte(endTime);
        }
        String targetLevel = alarmQuery.getTargetLevel();
        if(!StringUtil.isNUll(targetLevel)){
            criteria.and("targetLevel").is(targetLevel);
        }
        String targetLevelName = alarmQuery.getTargetLevelName();
        if(!StringUtil.isNUll(targetLevelName)){
            criteria.and("targetLevelName").is(targetLevelName);
        }
        //片键
        String sliceKey = alarmQuery.getSliceKey();
        if(!StringUtil.isNUll(sliceKey)){
            criteria.and("sliceKey").is(sliceKey);
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

    public int deleteByIdList(String table, List<String> alarmIdList){
        Criteria criteria = Criteria.where("_id").in(alarmIdList);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN();
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

    /**
     * @param alarmQuery
     * @param table
     * @auther: liudd
     * @date: 2019/9/26 10:15
     * 功能描述:获取一个告警
     */
    public Alarm getOne(AlarmQuery alarmQuery, String table) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, alarmQuery);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Alarm.class, table);
    }

    /**
     * @param table
     * @auther: liudd
     * @date: 2019/9/26 10:29
     * 功能描述:告警消除
     */
    public boolean resolve(AlarmQuery alarmQuery, String table, Date trecover) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, alarmQuery);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", alarmQuery.getState());
        update.set("trecover", trecover);
        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/14 14:43
     * 功能描述:获取实时告警，用于周期管理。
     */
    public List<Alarm> getAlarmList(String table, int size){
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria);
        query.limit(size);
        query.with(new Sort(Sort.Direction.ASC, "treport"));
        return mongoTemplate.find(query, Alarm.class, table);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/21 9:45
     * 功能描述:获取已存在告警。
     */
    public Alarm getExistAlarm(String sliceKey, String signalId, String serial, String state, String table){
        Criteria criteria = Criteria.where("sliceKey").is(sliceKey);
        if(!StringUtil.isNUll(signalId)) {
            criteria.and("signalId").is(signalId);
        }
        if(!StringUtil.isNUll(serial)){
            criteria.and("serial").is(serial);
        }
        if(!StringUtil.isNUll(state)){
            criteria.and("state").is(state);
        }
        Query query = Query.query(criteria);
         return mongoTemplate.findOne(query, Alarm.class, table);
    }

    public void save(List<Alarm> alarmList, String table){
        if(null != alarmList){
            for(Alarm alarm : alarmList){
                save(alarm, table);
            }
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/10/21 11:25
     * 功能描述:消除告警
     * 可能是实时告警，也可能是历史告警
     */
    public boolean resolve(String sliceKey, String signalId, String serial, String state, Date curDate, String table){
        Criteria criteria = Criteria.where("sliceKey");
        criteria.and("sliceKey").is(sliceKey);
        criteria.and("signalId").is(signalId);
        criteria.and("serial").is(serial);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", state);
        update.set("curDate", curDate);
        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;
    }

    public boolean updateAuxilary(String deviceType, String deviceModel, String deviceId,
                                  String signalId, String serial, Map<String, String> updateMap, String table){
        Criteria criteria = Criteria.where("deviceType").is(deviceType);
        criteria.and("deviceModel").is(deviceModel);
        criteria.and("deviceId").is(deviceId);
        criteria.and("signalId").is(signalId);
        criteria.and("serial").is(serial);
        Query query = Query.query(criteria);
        Update update = new Update();
        for(String key : updateMap.keySet()){
            update.set(key, updateMap.get(key));
        }
        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;

    }
}
