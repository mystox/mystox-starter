package com.kongtrolink.dao;

import com.kongtrolink.base.MongTable;
import com.kongtrolink.base.MongoUtil;
import com.kongtrolink.base.StringUtil;
import com.kongtrolink.enttiy.AlarmLevel;
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
 * @Date: 2019/9/11 15:05
 * @Description:
 */
@Repository
public class AlarmLevelDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.ALARM_LEVEL;

    public void save(AlarmLevel alarmLevel) {
        mongoTemplate.save(alarmLevel, table);
    }

    public boolean delete(String alarmLevelId) {
        Criteria criteria = Criteria.where("_id").is(alarmLevelId);
        Query query = Query.query(criteria);
        DeleteResult remove = mongoTemplate.remove(query, table);
        return remove.getDeletedCount()>0 ? true : false;
    }

    public boolean update(AlarmLevel alarmLevel) {
        Criteria criteria = Criteria.where("_id").is(alarmLevel.getId());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("sourceLevel", alarmLevel.getSourceLevel());
        update.set("targetLevel", alarmLevel.getTargetLevel());
        update.set("color", alarmLevel.getColor());
        update.set("updateTime", alarmLevel.getUpdateTime());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, table);
        return updateResult.getModifiedCount()>0 ? true : false;
    }

    public List<AlarmLevel> list(AlarmLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteira(levelQuery, criteria);
        Query query = Query.query(criteria);
        int currentPage = levelQuery.getCurrentPage();
        int pageSize = levelQuery.getPageSize();
        query.skip( (currentPage-1)*pageSize ).limit(pageSize);
        return mongoTemplate.find(query, AlarmLevel.class, table);
    }

    public int count(AlarmLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteira(levelQuery, criteria);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, table);
    }

    private Criteria baseCriteira(AlarmLevelQuery levelQuery, Criteria criteria) {
        String id = levelQuery.getId();
        if (!StringUtil.isNUll(id)) {
            criteria.and("_id").is(id);
        }
        String uniqueCode = levelQuery.getUniqueCode();
        if (!StringUtil.isNUll(uniqueCode)) {
            criteria.and("uniqueCode").is(uniqueCode);
        }
        String service = levelQuery.getService();
        if (!StringUtil.isNUll(service)) {
            criteria.and("service").is(service);
        }
        String deviceType = levelQuery.getDeviceType();
        if (!StringUtil.isNUll(deviceType)) {
            criteria.and("deviceType").is(deviceType);
        }
        String deviceName = levelQuery.getDeviceName();
        //liuddtodo 特殊字符处理，模糊查询处理
        if (!StringUtil.isNUll(deviceName)) {
            criteria.and("deviceName").is(deviceName);
        }
//        List<String> sourceLevelList = levelQuery.getSourceLevelList();
//        if(null != sourceLevelList){
//            StringBuilder stringBuilder = new StringBuilder();
//            for(String sourceLevel : sourceLevelList) {
//                sourceLevel = MongoUtil.escapeExprSpecialWord(sourceLevel);
//                stringBuilder.append("[1-9]{0,}").append(sourceLevel);
//            }
//            stringBuilder.append("[1-9]{0,}");
//            criteria.and("sourceLevelList").regex(stringBuilder.toString());
//        }
        String sourceLevel = levelQuery.getSourceLevel();
        if (!StringUtil.isNUll(sourceLevel)) {
            criteria.and("sourceLevel").is(sourceLevel);
        }
        String targetLevel = levelQuery.getTargetLevel();
        if (!StringUtil.isNUll(targetLevel)) {
            targetLevel = MongoUtil.escapeExprSpecialWord(targetLevel);
            criteria.and("targetLevel").is(targetLevel);
        }
        String color = levelQuery.getColor();
        if (!StringUtil.isNUll(color)) {
            criteria.and("color").is(color);
        }
        Date beginTime = levelQuery.getBeginTime();
        Date endTime = levelQuery.getEndTime();
        if (null != beginTime && null == endTime) {
            criteria.and("updateTime").gte(beginTime);
        } else if (null != beginTime && null != endTime) {
            criteria.and("updateTime").gte(beginTime).lte(endTime);
        } else if (null == beginTime && null != endTime) {
            criteria.and("updateTime").lte(endTime);
        }
        return criteria;
    }

    public AlarmLevel getOne(AlarmLevelQuery alarmLevelQuery) {
        Criteria criteria = new Criteria();
        baseCriteira(alarmLevelQuery, criteria);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, AlarmLevel.class, table);
    }
}
