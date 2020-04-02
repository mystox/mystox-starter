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
    private String table = "work_alarm_config";

    /**
     * 添加
     * @param workConfig
     */
    public void add(WorkAlarmConfig workConfig){
        mongoTemplate.save(workConfig, table);
    }

    /**
     * 根据企业编码和告警id获取记录
     * @param uniqueCode
     * @param alarmId
     * @return
     */
    public WorkAlarmConfig findByAlarmId(String uniqueCode, String alarmId){
        Criteria criteria = Criteria.where("alarmId").is(alarmId);
        criteria.and("uniqueCode").is(uniqueCode);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, WorkAlarmConfig.class, table);
    }

    /**
     * 根据告警id删除数据，
     * @param uniqueCode
     * @param alarmId
     * @return
     */
    public void deleteByAlarmId(String uniqueCode, String alarmId){
        Criteria criteria = Criteria.where("alarmId").is(alarmId);
        criteria.and("uniqueCode").is(uniqueCode);
        Query query = new Query(criteria);
        mongoTemplate.remove(query, table);
        return ;
    }
}
