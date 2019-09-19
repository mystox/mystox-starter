package com.kongtrolink.dao;

import com.kongtrolink.base.MongTable;
import com.kongtrolink.base.StringUtil;
import com.kongtrolink.enttiy.AlarmColor;
import com.kongtrolink.query.AlarmColorQuery;
import com.kongtrolink.query.AlarmQuery;
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
 * @Date: 2019/9/12 10:31
 * @Description:
 */
@Repository
public class AlarmColorDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = MongTable.ALARM_COLOR;

    public void add(AlarmColor alarmColor) {
        mongoTemplate.save(alarmColor, table);
    }

    public boolean delete(int alarmColorId) {
        Criteria criteria = Criteria.where("_id").is(alarmColorId);
        Query query = Query.query(criteria);
        DeleteResult remove = mongoTemplate.remove(query, table);
        return remove.getDeletedCount() > 0 ? true : false;
    }

    public boolean update(AlarmColor alarmColor) {
        Criteria criteria = Criteria.where("_id").is(alarmColor.getId());
        Query query = Query.query(criteria);
        Update update = new Update();
        update.set("color", alarmColor.getColor());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, table);
        return updateResult.getModifiedCount()>0 ? true : false;
    }

    public List<AlarmColor> list(AlarmColorQuery colorQuery) {
        Criteria criteria = new Criteria();
        createBaseCriteria(colorQuery, criteria);
        Query query = Query.query(criteria);
        int currentPage = colorQuery.getCurrentPage();
        int pageSize = colorQuery.getPageSize();
        query.skip( (currentPage-1) * pageSize ).limit(pageSize);
        return mongoTemplate.find(query, AlarmColor.class, table);
    }

    public int count(AlarmColorQuery colorQuery) {
        Criteria criteria = new Criteria();
        createBaseCriteria(colorQuery, criteria);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, table);
    }

    private Criteria createBaseCriteria(AlarmColorQuery colorQuery, Criteria criteria){
        String id = colorQuery.getId();
        if(!StringUtil.isNUll(id)){
            criteria.and("id").is(id);
        }
        String uniqueCode = colorQuery.getUniqueCode();
        if(!StringUtil.isNUll(uniqueCode)){
            criteria.and("uniqueCode").is(uniqueCode);
        }
        String level = colorQuery.getLevel();
        if(!StringUtil.isNUll(level)){
            criteria.and("level").is(level);
        }
        String color = colorQuery.getColor();
        if(!StringUtil.isNUll(color)){
            criteria.and("color").is(color);
        }
        return criteria;
    }

}
