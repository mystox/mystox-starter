package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.AlarmFocus;
import com.kongtrolink.framework.scloud.query.AlarmFocusQuery;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import sun.swing.StringUIClientPropertyKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/28 13:17
 * @Description:
 */
@Repository
public class AlarmFocusDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = "_alarm_focus";

    public boolean add(String uniqueCode, AlarmFocus alarmFocus) {
        mongoTemplate.save(alarmFocus, uniqueCode + table);
        if(!StringUtil.isNUll(alarmFocus.getId())){
            return true;
        }
        return false;
    }

    public boolean delete(String uniqueCode, String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, uniqueCode + table);
        return remove.getN() > 0 ? true : false;
    }

    public List<AlarmFocus> list(String uniqueCode, AlarmFocusQuery alarmFocusQuery) {
        Criteria criteria = new Criteria();
        baseCriteira(criteria, alarmFocusQuery);
        Query query = Query.query(criteria);
        int currentPage = alarmFocusQuery.getCurrentPage();
        int pageSize = alarmFocusQuery.getPageSize();
        query.skip( (currentPage-1) * pageSize).limit(pageSize);
        return mongoTemplate.find(query, AlarmFocus.class, uniqueCode + table);
    }

    private void baseCriteira(Criteria criteria, AlarmFocusQuery alarmFocusQuery){
        String userId = alarmFocusQuery.getUserId();
        if(!StringUtil.isNUll(userId)){
            criteria.and("userId").is(userId);
        }
        String beginTime = alarmFocusQuery.getBeginTime();
        String endTiem = alarmFocusQuery.getEndTiem();
        if(null != beginTime && null == endTiem){
            criteria.and("focusTime").gte(beginTime);
        }else if(null != beginTime && null != endTiem){
            criteria.and("focusTime").gte(beginTime).lte(endTiem);
        }else if(null == beginTime && null != endTiem){
            criteria.and("focusTime").lte(endTiem);
        }
    }

    public int count(String uniqueCode, AlarmFocusQuery alarmFocusQuery) {
        Criteria criteria = new Criteria();
        baseCriteira(criteria, alarmFocusQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + table);
    }



    public List<String> list2AlarmIdList(List<AlarmFocus> alarmFocusList) {
        if(null == alarmFocusList){
            return null;
        }
        List<String> alarmIdList = new ArrayList<>();
        for(AlarmFocus alarmFocus : alarmFocusList){
            alarmIdList.add(alarmFocus.getAlarmId());
        }
        return alarmIdList;
    }
}
