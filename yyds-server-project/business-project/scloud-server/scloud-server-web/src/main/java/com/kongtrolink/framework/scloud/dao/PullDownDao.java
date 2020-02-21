package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.Device;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.service.PullDownService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class PullDownDao{

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    RealTimeDataDao realTimeDataDao;
    /**
     * 设备类型 下拉框
     *
     * @param uniqueCode 企业编码
     * @return 设备类型列表
     */
    public List<DeviceType> getDeviceTypeList(String uniqueCode) {
        DBObject dbObject = new BasicDBObject();
        DBObject fieldObject = new BasicDBObject();
        fieldObject.put("code", true);
        fieldObject.put("typeName", true);
        Query query = new BasicQuery(dbObject, fieldObject);
        List<DeviceType> list = mongoTemplate.find(query,DeviceType.class, uniqueCode + CollectionSuffix.SIGNAL_TYPE);
        return list;
    }

    /**
     * 根据设备类型 获取该设备下的信号点列表
     *
     * @param uniqueCode 企业编码
     * @param deviceType 设备类型
     * @param type       遥测等类型
     * @return 信号点列表
     */

    public List<SignalType> getSignalTypeList(String uniqueCode, String deviceType, String type) {
        List<SignalType> list = new ArrayList<>();
        // 默认条件（无实际作用）
        Criteria criteria = Criteria.where("code").is(deviceType);
        Query query = new Query(criteria);
        DeviceType deviceTypeInfo =  mongoTemplate.findOne(query,DeviceType.class, uniqueCode + CollectionSuffix.SIGNAL_TYPE);
        if(deviceTypeInfo!=null && deviceTypeInfo.getSignalTypeList()!=null){
            for(SignalType signalType:deviceTypeInfo.getSignalTypeList()){
                if(type!=null && !type.equals(signalType.getType())){
                    continue;
                }
                list.add(signalType);
            }
        }
        return list;
    }

}
