package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.WorkAlarmConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * 告警工单配置对应表dao层
 */
@Repository
public class WorkAlarmConfigDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = "_work_alarm_config";

    /**
     * 添加
     * @param workConfig
     */
    public void add(String uniqueCode, WorkAlarmConfig workConfig){
        mongoTemplate.save(workConfig, uniqueCode + table);
    }

    /**
     * 根据企业编码和告警id获取记录
     * @param uniqueCode
     * @return
     */
    public WorkAlarmConfig findByAlarmKey(String uniqueCode, String alarmKey){
        Criteria criteria = Criteria.where("alarmKey").is(alarmKey);
        criteria.and("uniqueCode").is(uniqueCode);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, WorkAlarmConfig.class, table);
    }

    /**
     * 根据告警id删除数据，
     * @param uniqueCode
     * @return
     */
    public void deleteByAlarmKey(String uniqueCode, String alarmKey){
        Criteria criteria = Criteria.where("alarmKey").is(alarmKey);
        criteria.and("uniqueCode").is(uniqueCode);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, table);
        return ;
    }
}
