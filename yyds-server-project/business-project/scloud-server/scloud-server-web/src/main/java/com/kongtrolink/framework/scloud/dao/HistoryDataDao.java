package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.entity.HistoryDataEntity;
import com.kongtrolink.framework.scloud.entity.model.HistoryDataModel;
import com.kongtrolink.framework.scloud.query.HistoryDataQuery;
import com.kongtrolink.framework.scloud.service.HistoryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 历史数据功能类
 * @author Mag
 **/
@Repository
public class HistoryDataDao{

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 根据查询条件获取 历史数据列表 - 分页
     *
     * @param historyDataQuery 查询条件
     * @return 列表
     */
    public List<HistoryDataEntity> getHisList(String uniqueCode,HistoryDataQuery historyDataQuery) {
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
    public int getHisCount(String uniqueCode,HistoryDataQuery historyDataQuery) {
        Criteria criteria = getQueryCriteria(historyDataQuery);
        Query query = new Query(criteria);
        return (int)mongoTemplate.count(query, HistoryDataEntity.class, uniqueCode + CollectionSuffix.HISTORY);
    }

    private Criteria getQueryCriteria(HistoryDataQuery historyDataQuery){
        long startTime = historyDataQuery.getStartTime(); //开始时间
        long endTime = historyDataQuery.getEndTime();   //结束时间
        String fsuCode = historyDataQuery.getFsuCode();
        String deviceCode = historyDataQuery.getDeviceCode();
        String cntbId = historyDataQuery.getCntbId();//需要查询的具体信号点ID
        Criteria criteria = Criteria.where("time").gte(startTime).lte(endTime)
                .and("value."+cntbId).exists(true)
                .and("fsuCode").is(fsuCode)
                .and("deviceCode").is(deviceCode);
        return criteria;
    }
}
