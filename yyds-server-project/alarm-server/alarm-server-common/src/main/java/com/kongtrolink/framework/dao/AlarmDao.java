package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongoUtil;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.framework.query.AlarmQuery;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
    @Value("${alarm.currentLimit:5}")
    private int currentLimit;

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

    public List<DBObject> list(AlarmQuery alarmQuery, String table) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, alarmQuery);
        Query query = Query.query(criteria);
        int currentPage = alarmQuery.getCurrentPage();
        int pageSize = alarmQuery.getPageSize();
        query.with(new Sort(Sort.Direction.DESC, "treport"));
        query.skip( (currentPage-1)*pageSize ).limit(pageSize * (currentLimit+1));
        return mongoTemplate.find(query, DBObject.class, table);
    }

    public List<DBObject> getHistoryAlarmList(AlarmQuery alarmQuery, String table) {
        Criteria criteria = new Criteria();
        baseCriteria(criteria, alarmQuery);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "treport"));
        query.skip(alarmQuery.getRealBeginNum()).limit(alarmQuery.getRealLimit());

        return mongoTemplate.find(query, DBObject.class, table);
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
        String name = alarmQuery.getName();
        if(!StringUtil.isNUll(name)){
            name = MongoUtil.escapeExprSpecialWord(name);
            criteria.and("name").regex(".*?" + name + ".*?");
        }
        String targetLevelName = alarmQuery.getTargetLevelName();
        if(!StringUtil.isNUll(targetLevelName)){
            targetLevelName = MongoUtil.escapeExprSpecialWord(targetLevelName);
            criteria.and("targetLevelName").regex(".*?" + targetLevelName + ".*?");
        }
        String state = alarmQuery.getState();
        if(!StringUtil.isNUll(state)){
            criteria.and("state").is(state);
        }

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

        Date startBeginTime = alarmQuery.getStartBeginTime();
        Date startEndTime = alarmQuery.getStartEndTime();
        if(null != startBeginTime && null == startEndTime){
            criteria.and("treport").gte(startBeginTime);
        }else if(null != startBeginTime && null != startEndTime){
            criteria.and("treport").gte(startBeginTime).lte(startEndTime);
        }else if(null == startBeginTime && null != startEndTime){
            criteria.and("treport").lte(startEndTime);
        }

        Date clearBeginTime = alarmQuery.getClearBeginTime();
        Date clearEndTime = alarmQuery.getClearEndTime();
        if(null != clearBeginTime && null == clearEndTime){
            criteria.and("trecover").gte(clearBeginTime);
        }else if(null != clearBeginTime && null != clearEndTime){
            criteria.and("trecover").gte(clearBeginTime).lte(clearEndTime);
        }else if(null == clearBeginTime && null != clearEndTime){
            criteria.and("trecover").lte(clearEndTime);
        }
        String enterpriseCode = alarmQuery.getEnterpriseCode();
        if(!StringUtil.isNUll(enterpriseCode)) {
            criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String serverCode = alarmQuery.getServerCode();
        if(!StringUtil.isNUll(serverCode)){
            criteria.and("serverCode").is(serverCode);
        }
        return criteria;
    }

    public int deleteByIdList(String table, List<String> alarmIdList){
        Criteria criteria = Criteria.where("_id").in(alarmIdList);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN();
    }

    public boolean addList(List<Alarm> alarmList, String table) {
        // BulkMode.UNORDERED:表示并行处理，遇到错误时能继续执行不影响其他操作；BulkMode.ORDERED：表示顺序执行，遇到错误时会停止所有执行
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, table);
        for(Alarm alarm : alarmList) {
            ops.insert(alarm);
        }
        // 执行操作
        BulkWriteResult execute = ops.execute();
        int insertedCount = execute.getInsertedCount();
        return insertedCount>0 ? true : false;
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
    public List<Alarm> getCurrentAlarmList(String table, int size, Date overTime){
        //获取未标志的实时告警
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("hcTime").exists(false), Criteria.where("hcTime").lte(overTime));
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
    public Alarm getExistAlarm(String enterpriseCode, String serverCode, String deviceId, String signalId, String serial, String state, String table){
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        if(!StringUtil.isNUll(deviceId)){
            criteria.and("deviceId").is(deviceId);
        }
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

    /**
     * @auther: liudd
     * @date: 2019/10/21 11:25
     * 功能描述:消除告警
     * 可能是实时告警，也可能是历史告警
     */
    public boolean resolveByKey(String key, String state, Date curDate, String table){
        Criteria criteria = Criteria.where("key").is(key);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", state);
        update.set("trecover", curDate);
        WriteResult result = mongoTemplate.updateFirst(query, update, table);
        return result.getN()>0 ? true : false;
    }

    public Alarm getByKey(String key, String table){
        Criteria criteria = Criteria.where("key").is(key);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Alarm.class, table);
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

    public List<String> entity2IdList(List<Alarm> alarmList){
        List<String> alarmIdList = new ArrayList<>();
        if(null != alarmIdList){
            for(Alarm alarm : alarmList){
                alarmIdList.add(alarm.getId());
            }
        }
        return alarmIdList;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/30 17:35
     * 功能描述:修改告警表中一个属性值
     */
    public void updateHcTime(List<String> alarmIdList, Date overTime, String table){
        Criteria criteria = Criteria.where("_id").in(alarmIdList);
        Query query = Query.query(criteria);
        Update update = new Update();
        if(null == overTime) {
            update.unset("hcTime");
        }else{
            update.set("hcTime", overTime);
        }
        mongoTemplate.updateMulti(query, update, table);
    }
}
