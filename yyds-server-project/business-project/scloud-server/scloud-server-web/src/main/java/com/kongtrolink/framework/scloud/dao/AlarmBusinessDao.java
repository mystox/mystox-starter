package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.Statistics;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.BulkWriteResult;
import com.mongodb.WriteResult;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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
        update.set("state", BaseConstant.ALARM_STATE_RESOLVE);
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
        if(shield != null) {
            criteria.and("shield").is(shield);
        }else{
            criteria.and("shield").ne(true);
        }

        String name = businessQuery.getName();
        if(!StringUtil.isNUll(name)){
            name = MongoRegexUtil.escapeExprSpecialWord(name);
            criteria.and("name").regex(".*?" + name + ".*?");
        }
//        Integer level = businessQuery.getLevel();
//        if(null != level){
//            criteria.and("level").is(level);
//        }
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

    /**
     * @auther: liudd
     * @date: 2020/4/9 17:31
     * 功能描述:告警消除
     */
    public List<Statistics> countLevel(String uniqueCode, String table, AlarmBusinessQuery businessQuery) {
        Criteria criteria = baseCriteria(businessQuery);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("level").count().as("count"),
                Aggregation.project("_id", "count").and("_id").as("intPro")
        );
        AggregationResults<Statistics> aggregate = mongoTemplate.aggregate(aggregation, uniqueCode + table, Statistics.class);
        return aggregate.getMappedResults();
    }

    /**
     * 第一步：获取一个月内待处理告警前10的站点编码
     * 第二步：根据站点编码按日统计
     */
    public List<Statistics> siteDateCount(String uniqueCode, String table, AlarmBusinessQuery businessQuery) {
        businessQuery.setState(BaseConstant.ALARM_STATE_PENDING);
        Criteria oneCri = baseCriteria(businessQuery);
        Aggregation oneAggre = Aggregation.newAggregation(
                Aggregation.match(oneCri),
                Aggregation.group("siteCode").count().as("count"),
                Aggregation.project("count").and("_id").as("code"),
                Aggregation.sort(Sort.Direction.DESC, "count"),
                Aggregation.limit(10)
        );
        AggregationResults<Statistics> oneAggResults = mongoTemplate.aggregate(oneAggre, uniqueCode + table, Statistics.class);
        List<Statistics> oneMapped = oneAggResults.getMappedResults();
        List<String> siteCodeList = new ArrayList<>();
        for(Statistics alarmSiteStatistics : oneMapped){
            siteCodeList.add(alarmSiteStatistics.getCode());
        }
        businessQuery.setSiteCodeList(siteCodeList);
        Criteria twoCri = baseCriteria(businessQuery);
        Aggregation twoAggre = Aggregation.newAggregation(
                match(twoCri),
                project("siteCode")
                        .and("treport").plus(28800000).as("publishDateAdd"),
                project("siteCode").andExpression("substr(publishDateAdd,0,10)").as("checkTime"),//取得 时分秒
                group("checkTime","siteCode").count().as("count"),
                project("count").and("_id.checkTime").as("name").and("_id.siteCode").as("code"),
                sort(Sort.Direction.ASC, "code","name"));
        AggregationResults<Statistics> result = mongoTemplate.aggregate(twoAggre, uniqueCode + table, Statistics.class);
        return result.getMappedResults();
    }

    /**
     * @param uniqueCode
     * @param businessQuery
     * @auther: liudd
     * @date: 2020/4/15 11:19
     * 功能描述:告警分布
     */
    public List<Statistics> getAlarmDistributeList(String uniqueCode, AlarmBusinessQuery businessQuery) {
        businessQuery.setState(BaseConstant.ALARM_STATE_PENDING);
        String tierCodePrefix = businessQuery.getTierCodePrefix();
        if(!StringUtil.isNUll(tierCodePrefix) && tierCodePrefix.length() >= 6){
            return getAlarmSiteList(uniqueCode, businessQuery);
        }else {
            return getAlarmTierList(uniqueCode, businessQuery);
        }
    }

    /**
     * @param uniqueCode
     * @param businessQuery
     * @auther: liudd
     * @date: 2020/4/15 11:19
     * 功能描述:告警层级分布
     */
    public List<Statistics> getAlarmTierList(String uniqueCode, AlarmBusinessQuery businessQuery) {
        Criteria criteria = baseCriteria(businessQuery);
        int substrLen = 2;
        String tierCodePrefix = businessQuery.getTierCodePrefix();
        if(!StringUtil.isNUll(tierCodePrefix)) {
            criteria.and("siteCode").regex("^" + tierCodePrefix + ".*?");
            substrLen = tierCodePrefix.length() + 2;
        }
        String subStr = "substr(siteCode,0," + substrLen + ")";
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.project("siteCode").andExpression(subStr).as("tier"),
                Aggregation.group("tier").count().as("count"),
                Aggregation.sort(Sort.Direction.DESC, "count"),
                Aggregation.limit(8),
                Aggregation.project("_id", "count").and("_id").as("code")
        );
        AggregationResults<Statistics> aggregate = mongoTemplate.aggregate(aggregation, uniqueCode + CollectionSuffix.HIS_ALARM_BUSINESS, Statistics.class);
        //填充层级信息
        for(Statistics statistics : aggregate.getMappedResults()){
            statistics.setName("站点不存在，无法获取区域信息");
            Criteria siteCri = Criteria.where("code").regex("^" + statistics.getCode() + ".*?");
            Query siteQuery = Query.query(siteCri);
            SiteEntity siteEntity = mongoTemplate.findOne(siteQuery, SiteEntity.class, uniqueCode + CollectionSuffix.SITE);
            if(null != siteEntity){
                String tierName = siteEntity.getTierName();
                if(!StringUtil.isNUll(tierName)){
                    String[] split = tierName.split(BaseConstant.LEDGE);
                    statistics.setName(split[(substrLen/2)-1]);
                }
            }
        }
        return aggregate.getMappedResults();
    }

    /**
     * @param uniqueCode
     * @param businessQuery
     * @auther: liudd
     * @date: 2020/4/15 11:19
     * 功能描述:告警站点分布
     */
    public List<Statistics> getAlarmSiteList(String uniqueCode, AlarmBusinessQuery businessQuery) {
        Criteria criteria = baseCriteria(businessQuery);
        criteria.and("siteCode").regex("^" + businessQuery.getTierCodePrefix() + ".*?");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("siteCode").count().as("count"),
                Aggregation.sort(Sort.Direction.DESC, "count"),
                Aggregation.limit(8),
                Aggregation.project("_id", "count").and("_id").as("code")
        );
        AggregationResults<Statistics> aggregate = mongoTemplate.aggregate(aggregation, uniqueCode + CollectionSuffix.HIS_ALARM_BUSINESS, Statistics.class);
        return aggregate.getMappedResults();
    }
}
