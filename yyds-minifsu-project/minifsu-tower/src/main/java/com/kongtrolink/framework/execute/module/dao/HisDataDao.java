package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.entity.MongoDBTable;
import com.kongtrolink.framework.execute.module.model.HisData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fengw
 * 历史数据数据库操作
 * 新建文件 2019-5-8 14:03:14
 */
@Component
public class HisDataDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 添加历史数据信息
     * @param list 数据信息列表
     */
    public void insertList(List<HisData> list) {
        mongoTemplate.insert(list, MongoDBTable.T_HIS_DATA);
    }

    /**
     * 获取指定条件的历史数据信息
     * @param fsuId fsuId，为null或""则不作为查询条件
     * @param deviceId deviceId，为null或""则不作为查询条件
     * @param signalId signalId，为null或""则不作为查询条件
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 历史数据信息列表
     */
    public List<HisData> getList(String fsuId, String deviceId, String signalId, long startTime, long endTime) {
        Criteria criteria = Criteria
                .where("time").gt(startTime)
                .and("time").lt(endTime);
        if (fsuId != null && !fsuId.equals("")) {
            criteria.and("fsuId").is(fsuId);
        }
        if (deviceId != null && !deviceId.equals("")) {
            criteria.and("deviceId").is(deviceId);
        }
        if (signalId != null && !signalId.equals("")) {
            criteria.and("signalId").is(signalId);
        }
        return mongoTemplate.find(Query.query(criteria),
                HisData.class, MongoDBTable.T_HIS_DATA);
    }
}
