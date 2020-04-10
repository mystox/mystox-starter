package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.WorkRecord;
import com.kongtrolink.framework.scloud.query.WorkRecordQuery;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工单记录表mongo
 */
@Repository
public class WorkRecordDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = "_work_record";

    /**
     * 添加
     * @param uniqueCode
     * @param workRecord
     */
    public void add(String uniqueCode, WorkRecord workRecord){
        mongoTemplate.save(workRecord, uniqueCode + table);
    }

    /**
     * 查询列表
     * @param uniqueCode
     * @param recordQuery
     * @return
     */
    public List<WorkRecord> list(String uniqueCode, WorkRecordQuery recordQuery, Sort sort){
        Criteria criteria = createBaseCriteria(recordQuery);
        Query query = new Query(criteria);
        if(null != sort){
            query.with(sort);
        }
        return mongoTemplate.find(query, WorkRecord.class, uniqueCode + table);
    }

    /**
     * 创建基本查询条件
     * @param query
     * @return
     */
    private Criteria createBaseCriteria(WorkRecordQuery query){
        Criteria criteria = new Criteria();
        if(null == query){
            return criteria;
        }

        if(!StringUtil.isNUll(query.getWorkId())){
            criteria.and("workId").is(query.getWorkId());
        }
        if(!StringUtil.isNUll(query.getAlarmId())){
            criteria.and("alarmId").is(query.getAlarmId());
        }
        if(!StringUtil.isNUll(query.getOperatorId())){
            criteria.and("operator.strId").is(query.getOperatorId());
        }
        if(!StringUtil.isNUll(query.getOperatorPhone())){
            criteria.and("operatorPhone").is(query.getOperatorPhone());
        }
        return criteria;
    }

    /**
     * 获取工单最后一条工单记录
     * @param uniqueCode
     * @param workId
     * @return
     */
    public WorkRecord getLastWorkRecord(String uniqueCode, String workId){
        Criteria criteria = Criteria.where("workId").is(workId);
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "oeprateTime"));
        return mongoTemplate.findOne(query, WorkRecord.class, uniqueCode + table);
    }

    /**
     * 获取工单的所有处理记录
     * @param uniqueCode
     * @param workId
     * @return
     */
    public List<WorkRecord> getListByWorkId(String uniqueCode, String workId){
        Criteria criteria = Criteria.where("workId").is(workId);
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "oeprateTime"));
        return mongoTemplate.find(query, WorkRecord.class, uniqueCode + table);
    }
}
