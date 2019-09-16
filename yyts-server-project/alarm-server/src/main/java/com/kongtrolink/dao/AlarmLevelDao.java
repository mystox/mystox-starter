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
        update.set("sourceLevelList", alarmLevel.getSourceLevelList());
        update.set("targetLevel", alarmLevel.getTargetLevel());
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

    private Criteria baseCriteira(AlarmLevelQuery levelQuery, Criteria criteria){
        String id = levelQuery.getId();
        if(!StringUtil.isNUll(id)){
            criteria.and("_id").is(id);
        }
        String uniqueCode = levelQuery.getUniqueCode();
        if(!StringUtil.isNUll(uniqueCode)){
            criteria.and("uniqueCode").is(uniqueCode);
        }
        List<String> sourceLevelList = levelQuery.getSourceLevelList();
        if(null != sourceLevelList){
            StringBuilder stringBuilder = new StringBuilder();
            for(String sourceLevel : sourceLevelList) {
                sourceLevel = MongoUtil.escapeExprSpecialWord(sourceLevel);
                stringBuilder.append("[1-9]{0,}").append(sourceLevel);
            }
            stringBuilder.append("[1-9]{0,}");
            criteria.and("sourceLevelList").regex(stringBuilder.toString());
        }
        String targetLevel = levelQuery.getTargetLevel();
        if(!StringUtil.isNUll(targetLevel)){
            targetLevel = MongoUtil.escapeExprSpecialWord(targetLevel);
            criteria.and("targetLevel").is(targetLevel);
        }
        return criteria;
    }

    public List<AlarmLevel> getBySourceLevel(String sourceLevel) {
        sourceLevel = MongoUtil.escapeExprSpecialWord(sourceLevel);
        Criteria criteria = Criteria.where("sourceLevelList").regex(".*?" + sourceLevel + ".*?");
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, AlarmLevel.class, table);
    }

    /**
     * @param uniqueCode
     * @param sourceLevel
     * @auther: liudd
     * @date: 2019/9/16 16:53
     * 功能描述:根据告警原等级，获取告警自定义等级
     */
    public List<AlarmLevel> getTargetLevel(String uniqueCode, String sourceLevel) {
        Criteria criteria = Criteria.where("sourceLevelList").regex(".*?" + sourceLevel + ".*?");
        criteria.orOperator(Criteria.where("uniqueCode").exists(false), Criteria.where("uniqueCode").is(uniqueCode));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, AlarmLevel.class, table);
    }
}
