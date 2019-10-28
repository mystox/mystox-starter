package com.kongtrolink.framework.dao;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.base.MongoUtil;
import com.kongtrolink.framework.base.StringUtil;
import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        alarmLevel.initEntDevCode();
        mongoTemplate.save(alarmLevel, table);
    }

    public boolean update(AlarmLevel alarmLevel) {
        alarmLevel.initEntDevCode();
        Criteria criteria = Criteria.where("_id").is(alarmLevel.getId());
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        boolean result = remove.getN() > 0 ? true : false;
        if(result){
            mongoTemplate.save(alarmLevel, table);
        }
        return result;
    }

    public List<AlarmLevel> list(AlarmLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteira(levelQuery, criteria);
        Sort sort = new Sort(Sort.Direction.DESC, "entDevCode");
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),  //查询条件
                Aggregation.group("entDevCode"),
                Aggregation.sort(sort),
                Aggregation.skip((levelQuery.getCurrentPage() - 1) * levelQuery.getPageSize()),//跳到第几个开始
                Aggregation.limit(levelQuery.getPageSize())//查出多少个数据
        );
        AggregationResults<AlarmLevel> aggResult = mongoTemplate.aggregate(agg, table, AlarmLevel.class);
        List<AlarmLevel> mappedResults = aggResult.getMappedResults();
        List<String> entDevCodeList = new ArrayList<>();
        for(AlarmLevel alarmLevel : mappedResults){
            entDevCodeList.add(alarmLevel.getId());
        }
        List<AlarmLevel> byCodes = getByEntDevCodeList(entDevCodeList);
        List<AlarmLevel> resuList = new ArrayList<>();
        Map<String, AlarmLevel> codeEnterpriseMap = new HashMap<>();
        for(AlarmLevel alarmLevel : byCodes){
            String entDevCode = alarmLevel.getEntDevCode();
            AlarmLevel firEnter = codeEnterpriseMap.get(entDevCode);
            if(null == firEnter){
                alarmLevel.setSourceLevelList(new ArrayList<>());
                alarmLevel.getSourceLevelList().add(alarmLevel.getSourceLevel());
                alarmLevel.setTargetLevelList(new ArrayList<>());
                alarmLevel.getTargetLevelList().add(alarmLevel.getTargetLevel());
                alarmLevel.setTargetLevelNameList(new ArrayList<>());
                alarmLevel.getTargetLevelNameList().add(alarmLevel.getTargetLevelName());
                alarmLevel.setColorList(new ArrayList<>());
                alarmLevel.getColorList().add(alarmLevel.getColor());
                codeEnterpriseMap.put(entDevCode, alarmLevel);
                resuList.add(alarmLevel);
                continue;
            }
            firEnter.getSourceLevelList().add(alarmLevel.getSourceLevel());
            firEnter.getTargetLevelList().add(alarmLevel.getTargetLevel());
            firEnter.getTargetLevelNameList().add(alarmLevel.getTargetLevelName());
            firEnter.getColorList().add(alarmLevel.getColor());
        }
        return resuList;
    }

    public int count(AlarmLevelQuery levelQuery) {
        Criteria criteria = new Criteria();
        baseCriteira(levelQuery, criteria);
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),  //查询条件
                Aggregation.group("entDevCode")
        );
        AggregationResults<AlarmLevel> aggResult = mongoTemplate.aggregate(agg, table, AlarmLevel.class);
        List<AlarmLevel> mappedResults = aggResult.getMappedResults();
        return mappedResults.size();
    }

    private Criteria baseCriteira(AlarmLevelQuery levelQuery, Criteria criteria) {
        String id = levelQuery.getId();
        if (!StringUtil.isNUll(id)) {
            criteria.and("_id").is(id);
        }
        String enterpriseCode = levelQuery.getEnterpriseCode();
        if (!StringUtil.isNUll(enterpriseCode)) {
            criteria.and("enterpriseCode").is(enterpriseCode);
        }
        String enterpriseName = levelQuery.getEnterpriseName();
        if(!StringUtil.isNUll(enterpriseName)){
            enterpriseName = MongoUtil.escapeExprSpecialWord(enterpriseName);
            criteria.and("enterpriseName").regex(".*?" + enterpriseName + ".*?");
        }
        String serverCode = levelQuery.getServerCode();
        if (!StringUtil.isNUll(serverCode)) {
            criteria.and("serverCode").is(serverCode);
        }
        String serverName = levelQuery.getServerName();
        if(!StringUtil.isNUll(serverName)){
            serverName = MongoUtil.escapeExprSpecialWord(serverName);
            criteria.and("serverName").regex(".*?" + serverName + ".*?");
        }
        String operatorName = levelQuery.getOperatorName();
        if(!StringUtil.isNUll(operatorName)){
            operatorName = MongoUtil.escapeExprSpecialWord(operatorName);
            criteria.and("operator.name").regex(".*?" + operatorName + ".*?");
        }
        String deviceType = levelQuery.getDeviceType();
        if (!StringUtil.isNUll(deviceType)) {
            deviceType = MongoUtil.escapeExprSpecialWord(deviceType);
            criteria.and("deviceType").regex(".*?" + deviceType + ".*?");
        }
        String deviceModel = levelQuery.getDeviceModel();
        if (!StringUtil.isNUll(deviceModel)) {
            deviceModel = MongoUtil.escapeExprSpecialWord(deviceModel);
            criteria.and("deviceModel").regex(".*?" + deviceModel + ".*?");
        }
        String generate = levelQuery.getGenerate();
        if(!StringUtil.isNUll(generate)){
            criteria.and("generate").is(generate);
        }
        return criteria;
    }

    /**
     * @param enterpriseCode
     * @param serverCode
     * @param deviceType
     * @param deviceModel
     * @auther: liudd
     * @date: 2019/10/16 15:41
     * 功能描述:根据设备类型信息删除告警等级
     */
    public int deleteList(String enterpriseCode, String serverCode, String deviceType, String deviceModel) {
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        if(!StringUtil.isNUll(deviceType)) {
            criteria.and("deviceType").is(deviceType);
        }
        if(!StringUtil.isNUll(deviceModel)) {
            criteria.and("deviceModel").is(deviceModel);
        }
        Query query = Query.query(criteria);
        WriteResult remove = mongoTemplate.remove(query, table);
        return remove.getN();
    }

    /**
     * @auther: liudd
     * @date: 2019/10/12 14:31
     * 功能描述:等级入口使用，匹配告警
     */
    public AlarmLevel matchLevel(String enterpriseCode, String serverCode, String deviceType, String deviceModel, Integer sourceLevel){
        Criteria criteria = Criteria.where("enterpriseCode").is(enterpriseCode);
        criteria.and("serverCode").is(serverCode);
        criteria.and("deviceType").is(deviceType);
        criteria.and("deviceModel").is(deviceModel);
        criteria.and("sourceLevel").is(sourceLevel);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, AlarmLevel.class, table);
    }

    public List<AlarmLevel> getByEntDevCodeList(List<String> entDevCodeList){
        Criteria criteria = Criteria.where("entDevCode").in(entDevCodeList);
        Query query = Query.query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "level"));
        return mongoTemplate.find(query, AlarmLevel.class, table);
    }
}
