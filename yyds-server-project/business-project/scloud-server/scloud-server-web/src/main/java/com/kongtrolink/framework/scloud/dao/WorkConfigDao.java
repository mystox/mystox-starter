package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.entity.WorkConfig;
import com.kongtrolink.framework.scloud.query.WorkConfigQuery;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 10:39
 * @Description:
 */
@Repository
public class WorkConfigDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = "_work_config";

    public void add(String uniqueCode, WorkConfig workConfig) {
        mongoTemplate.save(workConfig, uniqueCode + table);
    }

    public boolean delete(String uniqueCode, String workConfigId) {
        Criteria criteria = Criteria.where("_id").is(workConfigId);
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, uniqueCode + table);
        return remove.getN()>0 ? true : false;
    }

    public List<WorkConfig> list(String uniqueCode, WorkConfigQuery workConfigQuery) {
        Criteria criteria = baseCriteria(workConfigQuery);
        Query query = Query.query(criteria);
        int currentPage = workConfigQuery.getCurrentPage();
        int pageSize = workConfigQuery.getPageSize();
        query.skip( (currentPage-1) * pageSize ).limit(pageSize);
        return mongoTemplate.find(query, WorkConfig.class, uniqueCode + table);
    }

    Criteria baseCriteria(WorkConfigQuery workConfigQuery){
        Criteria criteria = new Criteria();

        return criteria;
    }

    public int count(String uniqueCode, WorkConfigQuery workConfigQuery) {
        Criteria criteria = baseCriteria(workConfigQuery);
        Query query = Query.query(criteria);
        return (int)mongoTemplate.count(query, uniqueCode + table);
    }

    public WorkConfig getById(String uniqueCode, String workConfigId) {
        Criteria criteria = Criteria.where("_id").is(workConfigId);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, WorkConfig.class, uniqueCode + table);
    }

    /**
     * 获取最告警条件的告警工单配置，这里还需要根据告警级别，告警名称来匹配
     * 告警级别和告警名称需要分开查询
     * 1，先获取告警级别的工单配置
     * 2，获取告警名称的工单配置
     * 3，如果两个都有，比较上报时间，取最晚的
     * 4，如果两个都没有，使用默认告警工单配置
     * @param uniqueCode
     * @param configQuery
     * @return
     */
    public WorkConfig matchAutoConfig(String uniqueCode, WorkConfigQuery configQuery){
        WorkConfig workConfig = null;
        //根据等级获取
        Criteria levelCriteria = new Criteria();
        matchBaseCriteria(levelCriteria, configQuery);
        levelCriteria.and("alarmLevelList").is(configQuery.getAlarmLevel());
        Query levelQuery= new Query(levelCriteria);
        levelQuery.with(new Sort(Sort.Direction.DESC, "updateTime"));
        WorkConfig levelConfig = mongoTemplate.findOne(levelQuery, WorkConfig.class, uniqueCode + table);

        //根据告警名称获取
        Criteria alarmNameCriteria = new Criteria();
        matchBaseCriteria(alarmNameCriteria, configQuery);
        alarmNameCriteria.and("alarmNameList").is(configQuery.getAlarmName());
        Query alarmNameQuery = new Query(alarmNameCriteria);
        alarmNameQuery.with(new Sort(Sort.Direction.DESC, "updateTime"));
        WorkConfig alarmNameConfig = mongoTemplate.findOne(alarmNameQuery, WorkConfig.class, uniqueCode + table);

        if(null != levelConfig && null == alarmNameConfig){
            workConfig = levelConfig;
        }else if(null == levelConfig && null != alarmNameConfig){
            workConfig = alarmNameConfig;
        }else if (null != levelConfig && null != alarmNameConfig){
            if(levelConfig.getUpdateTime().getTime() > alarmNameConfig.getUpdateTime().getTime()){
                workConfig = levelConfig;
            }else{
                workConfig = alarmNameConfig;
            }
        }
        return workConfig;
    }


    private Criteria matchBaseCriteria(Criteria baseCriteria, WorkConfigQuery configQuery){
        baseCriteria.and("siteTypeList").is(configQuery.getSiteType());
        baseCriteria.and("reportBeginInt").lte(configQuery.getIntTreport());
        baseCriteria.and("reportEndInt").gte(configQuery.getIntTreport());
        String siteCode = configQuery.getSiteCode();
        baseCriteria.and("siteCodeList").is(siteCode);
        return baseCriteria;
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 10:40
     * 功能描述:手动派单匹配工单配置
     */
    public WorkConfig matchManualConfig(String uniqueCode, String siteCode) {
        Criteria criteria = Criteria.where("sendType").is(WorkConstants.SEND_TYPE_MANUAL);
        criteria.and("siteCodeList").is(siteCode);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return mongoTemplate.findOne(query, WorkConfig.class, uniqueCode + table);
    }

    /**
     * @param uniqueCode
     * @auther: liudd
     * @date: 2020/4/2 10:47
     * 功能描述:获取默认告警工单配置，如果么有，则创建并返回
     */
    public WorkConfig getDefaultConfig(String uniqueCode) {
        Criteria criteria = Criteria.where("sendType").is(WorkConstants.WORK_CONFIG_TYPE_DEFAULT);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, WorkConfig.class, uniqueCode + table);
    }

}
