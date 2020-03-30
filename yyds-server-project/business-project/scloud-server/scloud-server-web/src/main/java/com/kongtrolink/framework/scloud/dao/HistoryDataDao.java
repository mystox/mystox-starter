package com.kongtrolink.framework.scloud.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.base.CustomOperation;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.HistoryDataEntity;
import com.kongtrolink.framework.scloud.entity.model.HistoryDataDayModel;
import com.kongtrolink.framework.scloud.query.HistoryDataQuery;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * 历史数据功能类
 *
 * @author Mag
 **/
@Repository
public class HistoryDataDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 根据查询条件获取 历史数据列表 - 分页
     *
     * @param historyDataQuery 查询条件
     * @return 列表
     */
    public List<HistoryDataEntity> getHisList(String uniqueCode, HistoryDataQuery historyDataQuery) {
        Criteria criteria = getQueryCriteria(historyDataQuery);
        int currentPage = historyDataQuery.getCurrentPage();
        int pageSize = historyDataQuery.getPageSize();
        Query query = new Query(criteria);
        query.skip((currentPage - 1) * pageSize).limit(pageSize);
        return mongoTemplate.find(query, HistoryDataEntity.class, uniqueCode + CollectionSuffix.HISTORY);
    }

    /**
     * 根据查询条件获取 历史数据列表 - 分页-总数
     *
     * @param historyDataQuery 查询条件
     * @return 列表总数
     */
    public int getHisCount(String uniqueCode, HistoryDataQuery historyDataQuery) {
        Criteria criteria = getQueryCriteria(historyDataQuery);
        Query query = new Query(criteria);
        return (int) mongoTemplate.count(query, HistoryDataEntity.class, uniqueCode + CollectionSuffix.HISTORY);
    }

    private Criteria getQueryCriteria(HistoryDataQuery historyDataQuery) {
        long startTime = historyDataQuery.getStartTime(); //开始时间
        long endTime = historyDataQuery.getEndTime();   //结束时间
        String deviceCode = historyDataQuery.getDeviceCode();
        String cntbId = historyDataQuery.getCntbId();//需要查询的具体信号点ID
        Criteria criteria = Criteria.where("time").gte(startTime).lte(endTime)
                .and("deviceCode").is(deviceCode);
        if (cntbId != null && !"".equals(cntbId)) {
            criteria.and("value." + cntbId).exists(true);
        }
        return criteria;
    }

    /**
     * 根据查询条件获取历史数据统计数据 最大值 最小值 平均值
     *
     * @param uniqueCode       企业编码
     * @param historyDataQuery 查询条件
     * @return 统计数据
     */
    public List<HistoryDataDayModel> getDayReport(String uniqueCode, HistoryDataQuery historyDataQuery) {
        Criteria criteria = getQueryCriteria(historyDataQuery);
        String cntbId = historyDataQuery.getCntbId();
        String valueField = "value." + cntbId;
        String valueFieldKey = "$" + valueField;
        try {
            BasicDBObject projectSql = new BasicDBObject(
                    "$project", new BasicDBObject(
                    "value", new BasicDBObject(
                    "$toDouble", valueFieldKey
            )
            ).append(
                    "time", new BasicDBObject(
                            "$add", new Object[]{new Date(0), "$time"}
                    )
            ));
            Aggregation agg = Aggregation.newAggregation(
                    match(criteria),
                    new CustomOperation(projectSql), //取得字段
                    project("value").and("time").plus(28800000).as("time"),
                    project("value").and(DateOperators.DateToString.dateOf("time").toString("%Y-%m-%d")).as("time"),
                    group("time").avg("value").as("avgValue")
                            .max("value").as("maxValue")
                            .min("value").as("minValue"),
                    project("avgValue", "maxValue", "minValue").and("time").previousOperation(),
                    sort(Sort.Direction.DESC, "time")
            );
            AggregationResults<HistoryDataDayModel> result = mongoTemplate.aggregate(agg, uniqueCode + CollectionSuffix.HISTORY, HistoryDataDayModel.class);
            return result.getMappedResults();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public JSONObject statisticElectricHistoryData(String enterpriseCode, String deviceCode, String mobileSignalCntb, String unicomSignalCntb, String telecomSignalCntb, long startBeginTime, long startEndTime) {
        JSONObject result = new JSONObject();
        Criteria criteria = Criteria.where("time").gte(startBeginTime).lte(startEndTime)
                .and("deviceCode").is(deviceCode);
        Query query = Query.query(criteria);
        HistoryDataEntity historyDataEntityStart = mongoTemplate.findOne(query, HistoryDataEntity.class, enterpriseCode + CollectionSuffix.HISTORY);
        Map<String, Object> startDataMap = historyDataEntityStart.getValue();
        Object mobileSignalCntbStartValue = startDataMap.get("mobileSignalCntb");
        if (mobileSignalCntbStartValue!=null) result.put("sMobileElecCount", mobileSignalCntbStartValue);
        Object unicomSignalCntbStartValue = startDataMap.get("unicomSignalCntb");
        if (unicomSignalCntbStartValue!=null) result.put("sUnicomElecCount", unicomSignalCntbStartValue);
        Object telecomSignalCntbStartValue = startDataMap.get("telecomSignalCntb");
        if (telecomSignalCntbStartValue!=null) result.put("sTelecomElecCount", telecomSignalCntbStartValue);

        query.with(new Sort(Sort.Direction.DESC, "time"));
        HistoryDataEntity historyDataEntityEnd = mongoTemplate.findOne(query, HistoryDataEntity.class, enterpriseCode + CollectionSuffix.HISTORY);
        Map<String, Object> endDataMap = historyDataEntityEnd.getValue();
        Object mobileSignalCntbEndValue = endDataMap.get("mobileSignalCntb");
        if (mobileSignalCntbEndValue!=null) result.put("eMobileElecCount", mobileSignalCntbEndValue);
        Object unicomSignalCntbEndValue = endDataMap.get("unicomSignalCntb");
        if (unicomSignalCntbEndValue!=null) result.put("eUnicomElecCount", unicomSignalCntbEndValue);
        Object telecomSignalCntbEndValue = endDataMap.get("telecomSignalCntb");
        if (telecomSignalCntbEndValue!=null) result.put("eTelecomElecCount", telecomSignalCntbEndValue);

        return result;
    }
}
