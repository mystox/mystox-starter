package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.SignalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 信号点数据操作类
 * Created by Eric on 2020/2/10.
 */
@Repository
public class SignalMongo  {

    @Autowired
    MongoTemplate mongoTemplate;
    private String table = CollectionSuffix.SIGNAL_TYPE;

    /**
     * 修改信号类型映射表
     */
    public void modifyTypeList(String uniqueCode, List<DeviceType> deviceTypes) {
        mongoTemplate.dropCollection(uniqueCode + CollectionSuffix.SIGNAL_TYPE);
        for (DeviceType deviceType : deviceTypes) {
            mongoTemplate.save(deviceType, uniqueCode + CollectionSuffix.SIGNAL_TYPE);
        }
    }

    /**
     * 查询企业设备信号类型映射表
     */
    public List<DeviceType> findSignalTypeList(String uniqueCode){
        return mongoTemplate.findAll(DeviceType.class, uniqueCode + CollectionSuffix.SIGNAL_TYPE);
    }

    /**
     * @param uniqueCode
     * @auther: liudd
     * @date: 2020/3/3 11:06
     * 功能描述:根据设备类型编码获取deviceType
     */
    public DeviceType getByCode(String uniqueCode, String typeCode) {
        Criteria criteria = Criteria.where("code").is(typeCode);
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, DeviceType.class, uniqueCode + CollectionSuffix.SIGNAL_TYPE);
    }

    /**
     * @param uniqueCode
     * @param codeList
     * @param cntbIdList
     * @auther: liudd
     * @date: 2020/3/3 16:23
     * 功能描述:根据设备类型和cntbid列表，获取signalType列表
     */
    public List<SignalType> getByCodeListCntbIdList(String uniqueCode, List<String> codeList, List<String> cntbIdList) {
        Criteria codeCri = Criteria.where("code").in(codeList);
        Criteria cntbIdCri = Criteria.where("cntbId").in(cntbIdList);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(codeCri),
                Aggregation.unwind("signalTypeList"),
                Aggregation.match(cntbIdCri)
        );
        AggregationResults<SignalType> aggregate = mongoTemplate.aggregate(aggregation, uniqueCode + table, SignalType.class);
        return aggregate.getMappedResults();
    }
}
