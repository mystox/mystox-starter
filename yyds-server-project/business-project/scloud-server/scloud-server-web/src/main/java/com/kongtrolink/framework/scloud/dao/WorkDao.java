package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.Work;
import com.kongtrolink.framework.scloud.query.WorkQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 13:28
 * @Description:
 */
@Repository
public class WorkDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = "_work";

    public void add(String uniqueCode, Work work) {
        mongoTemplate.save(work, uniqueCode + table);
    }

    public boolean delete(String uniqueCode, String workId) {
        Criteria criteria = Criteria.where("_id").is(workId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, uniqueCode + table);
        return remove.getN()>0? true : false;
    }

    public Work getById(String uniqueCode, String workId) {
        Criteria criteria = Criteria.where("_id").is(workId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Work.class, uniqueCode + table);
    }

    public List<Work> list(String uniqueCode, WorkQuery workQuery) {
        Criteria criteria = baseCriteria(workQuery);
        Query query = Query.query(criteria);
        int currentPage = workQuery.getCurrentPage();
        int pageSize = workQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);
        return mongoTemplate.find(query, Work.class, uniqueCode + table);
    }

    Criteria baseCriteria(WorkQuery workQuery){
        Criteria criteria = new Criteria();
        List<String> siteCodeList = workQuery.getSiteCodeList();
        if(null != siteCodeList){
            criteria.and("site.strId").in(siteCodeList);
        }
        String sendType = workQuery.getSendType();
        if(!StringUtil.isNUll(sendType)){
            criteria.and("sendType").is(sendType);
        }
        String status = workQuery.getStatus();
        if(!StringUtil.isNUll(status)){
            criteria.and("status").is(status);
        }
        String code = workQuery.getCode();
        if(!StringUtil.isNUll(code)){
            code = MongoRegexUtil.escapeExprSpecialWord(code);
            criteria.and("code").regex(".*?" + code + ".*?");
        }
        String siteName = workQuery.getSiteName();
        if(!StringUtil.isNUll(siteName)){
            siteName = MongoRegexUtil.escapeExprSpecialWord(siteName);
            criteria.and("siteName").regex(".*?" + siteName + ".*?");
        }
        String deviceType = workQuery.getDeviceType();
        if(!StringUtil.isNUll(deviceType)){
            criteria.and("deviceType").is(deviceType);
        }
        //告警类型先不处理
        String alarmLevel = workQuery.getAlarmLevel();
        if(!StringUtil.isNUll(alarmLevel)){
            criteria.and("alarmLevelList").is(alarmLevel);
        }
        String companyName = workQuery.getCompanyName();
        if(!StringUtil.isNUll(companyName)){
            companyName = MongoRegexUtil.escapeExprSpecialWord(companyName);
            criteria.and("companyName").regex(".*?" + companyName + ".*?");
        }
        Date sentTimeBegin = workQuery.getSentTimeBegin();
        Date sentTimeEnd = workQuery.getSentTimeEnd();
        if(null != sentTimeBegin && null == sentTimeEnd){
            criteria.and("sentTime").gte(sentTimeBegin);
        }else if(null != sentTimeBegin && null != sentTimeEnd){
            criteria.and("sentTime").gte(sentTimeBegin).lte(sentTimeEnd);
        }else if(null == sentTimeBegin && null != sentTimeEnd){
            criteria.and("sentTime").lte(sentTimeEnd);
        }
        return criteria;
    }

    public int count(String uniqueCode, WorkQuery workQuery) {
        Criteria criteria = baseCriteria(workQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + table);
    }

    public Work getNotOverByDeviceCode(String uniqueCode, String deviceCode) {
        Criteria criteria = Criteria.where("device.strId").is(deviceCode);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Work.class, uniqueCode + table);
    }

    public List<Work> listByKeys(String uniqueCode, List<String> keys){
        Criteria criteria = Criteria.where("workAlarmList.alarmKey").in(keys);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, Work.class, uniqueCode + table);
    }

    public void updateAlarmInfo(String uniqueCode, Work work){
        Criteria criteria = Criteria.where("_id").is(work.getId());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("workAlarmList", work.getWorkAlarmList());
        update.set("alarmState", work.getAlarmState());
        update.set("pendingCount", work.getPendingCount());
        mongoTemplate.updateFirst(query, update, uniqueCode + table);
    }
}
