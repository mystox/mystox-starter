package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.BulkWriteResult;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 18:46
 * @Description:
 */
@Repository
public class AlarmBusinessDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public void add(String uniqueCode, String table, AlarmBusiness business) {
        mongoTemplate.save(business, uniqueCode + table );
    }

    public boolean add(String uniqueCode, String table, List<AlarmBusiness> businessList) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, uniqueCode + table );
        for(AlarmBusiness business : businessList){
            bulkOperations.insert(business);
        }
        BulkWriteResult execute = bulkOperations.execute();
        return execute.getInsertedCount()>0 ? true : false;
    }

    public List<AlarmBusiness> listByKeyList(String uniqueCode, String table, List<String> keyList) {
        Criteria criteria = Criteria.where("key").in(keyList);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, AlarmBusiness.class, uniqueCode + table);
    }


    public boolean deleteByKeyList(String uniqueCode, String table, List<String> keyList) {
        Criteria criteria = Criteria.where("key").in(keyList);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, uniqueCode + table);
        return remove.getN()>0 ? true : false;
    }

    /**
     * @param unuqieCode
     * @param table
     * @param alarmBusiness
     * @auther: liudd
     * @date: 2020/4/9 11:15
     * 功能描述:根据key消除告警
     */
    public boolean resolveByKey(String unuqieCode, String table, AlarmBusiness alarmBusiness) {
        Criteria criteria = Criteria.where("key").is(alarmBusiness.getKey());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("trecover", alarmBusiness.getTrecover());
        update.set("state", alarmBusiness.getState());
        WriteResult result = mongoTemplate.updateFirst(query, update, unuqieCode + table);
        return result.getN()>0 ? true : false;
    }

    public List<AlarmBusiness> list(String uniqueCode, String table, AlarmBusinessQuery alarmBusinessQuery) {
        Criteria criteria = baseCriteria(alarmBusinessQuery);
        Query query = Query.query(criteria);
        int currentPage = alarmBusinessQuery.getCurrentPage();
        int pageSize = alarmBusinessQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(alarmBusinessQuery.getSkipSize());
        return mongoTemplate.find(query, AlarmBusiness.class, uniqueCode + table);
    }

    public int count(String uniqueCode, String table, AlarmBusinessQuery alarmBusinessQuery) {
        Criteria criteria = baseCriteria(alarmBusinessQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query,  uniqueCode + table);
    }

    private Criteria baseCriteria(AlarmBusinessQuery businessQuery){
        Criteria criteria = new Criteria();
        Boolean shield = businessQuery.getShield();
        criteria.and("shield").is(shield);
        String name = businessQuery.getName();
        if(!StringUtil.isNUll(name)){
            name = MongoRegexUtil.escapeExprSpecialWord(name);
            criteria.and("name").regex(".*?" + name + ".*?");
        }
        Integer level = businessQuery.getLevel();
        if(null != level){
            criteria.and("level").is(level);
        }
        List<Integer> levelList = businessQuery.getLevelList();
        if(null != levelList){
            criteria.and("level").in(levelList);
        }
        String state = businessQuery.getState();
        if(!StringUtil.isNUll(state)){
            criteria.and("state").is(state);
        }
        String checkState = businessQuery.getCheckState();
        if(!StringUtil.isNUll(checkState)){
            criteria.and("checkState").is(checkState);
        }
        List<String> tierCodeList = businessQuery.getTierCodeList();
        if(null != tierCodeList){
            criteria.and("tierCode").in(tierCodeList);
        }
        List<String> siteCodeList = businessQuery.getSiteCodeList();
        if(null != siteCodeList){
            criteria.and("siteCode").in(siteCodeList);
        }
        String deviceType = businessQuery.getDeviceType();
        if(!StringUtil.isNUll(deviceType)){
            criteria.and("deviceType").is(deviceType);
        }
        List<String> deviceTypeList = businessQuery.getDeviceTypeList();
        if(null != deviceTypeList){
            criteria.and("deviceType").in(deviceTypeList);
        }
        String deviceModel = businessQuery.getDeviceModel();
        if(!StringUtil.isNUll(deviceModel)){
            criteria.and("deviceModel").is(deviceModel);
        }
        List<String> deviceCodeList = businessQuery.getDeviceCodeList();
        if(null != deviceCodeList){
            criteria.and("deviceCode").in(deviceCodeList);
        }
        Date startBeginTime = businessQuery.getStartBeginTime();
        Date startEndTime = businessQuery.getStartEndTime();
        if(null != startBeginTime && null == startEndTime){
            criteria.and("treport").gte(startBeginTime);
        }else if(null != startBeginTime && null != startEndTime){
            criteria.and("treport").gte(startBeginTime).lte(startEndTime);
        }else if(null == startBeginTime && null != startEndTime){
            criteria.and("treport").lte(startEndTime);
        }
        Date clearBeginTime = businessQuery.getClearBeginTime();
        Date clearEndTime = businessQuery.getClearEndTime();
        if(null != clearBeginTime && null == clearEndTime){
            criteria.and("trecover").gte(clearBeginTime);
        }else if(null != clearBeginTime && null != clearEndTime){
            criteria.and("trecover").gte(clearBeginTime).lte(clearEndTime);
        }else if(null == clearBeginTime && null != clearEndTime){
            criteria.and("trecover").lte(clearEndTime);
        }
        List<String> entDevSigList = businessQuery.getEntDevSigList();
        if(null != entDevSigList){
            criteria.and("entDevSig").in(entDevSigList);
        }
        return criteria;
    }

    /**
     * @param uniqueCode
     * @param businessQuery
     * @auther: liudd
     * @date: 2020/4/9 16:26
     * 功能描述:告警确认
     */
    public int check(String uniqueCode, String table, AlarmBusinessQuery businessQuery) {
        Criteria criteria = Criteria.where("key").in(businessQuery.getKeyList());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("checkState", BaseConstant.CHECKED);
        update.set("checkTime", businessQuery.getOperateTime());
        update.set("checkContant", businessQuery.getOperateDesc());
        update.set("checker", new FacadeView(businessQuery.getOperateUserId(), businessQuery.getOperateUsername()));
        WriteResult result = mongoTemplate.updateMulti(query, update, uniqueCode + table);
        return result.getN();
    }

    public int unCheck(String uniqueCode, String table, AlarmBusinessQuery businessQuery) {
        Criteria criteria = Criteria.where("key").in(businessQuery.getKeyList());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("checkState", BaseConstant.NOCHECK);
        update.unset("checkTime");
        update.unset("checkContant");
        update.unset("checker");
        WriteResult result = mongoTemplate.updateMulti(query, update, uniqueCode + table);
        return result.getN();
    }

    /**
     * @param uniqueCode
     * @param table
     * @param businessQuery
     * @auther: liudd
     * @date: 2020/4/9 17:31
     * 功能描述:单条消除告警
     */
    public boolean resolve(String uniqueCode, String table, AlarmBusinessQuery businessQuery) {
        Criteria criteria = Criteria.where("key").is(businessQuery.getKey());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", BaseConstant.ALARM_STATE_RESOLVE);
        update.set("trecover", businessQuery.getOperateTime());
        WriteResult result = mongoTemplate.updateMulti(query, update, uniqueCode + table);
        return result.getN()>0 ? true : false;
    }

    public boolean unResolveByKeys(String uniqueCode, String table, List<String> keyList) {
        Criteria criteria = Criteria.where("key").in(keyList);
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("state", BaseConstant.ALARM_STATE_PENDING);
        update.unset("trecover");
        WriteResult result = mongoTemplate.updateMulti(query, update, uniqueCode + table);
        return result.getN()>0 ? true : false;
    }
}
