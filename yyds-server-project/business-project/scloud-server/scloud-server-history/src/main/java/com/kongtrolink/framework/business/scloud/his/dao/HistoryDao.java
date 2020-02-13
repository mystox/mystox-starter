package com.kongtrolink.framework.business.scloud.his.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.HistoryDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HistoryDao {
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 历史数据保存 每天保存一个表
     */
    public void saveHistory(String uniqueCode,HistoryDataEntity historyDataEntity,String tableTime){
        mongoTemplate.save(historyDataEntity,uniqueCode+ CollectionSuffix.HISTORY+ "_"+tableTime);
    }
}
