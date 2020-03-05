package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.DeviceEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备资产 相关数据查询类
 * Created by Eric on 2020/2/12.
 */
@Repository
public class DeviceMongo {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 获取单个站点下所有设备
     */
    public List<DeviceEntity> findAllDevicesBySiteCode(String uniqueCode, String siteCode){
        Criteria criteria = new Criteria();
        if (siteCode != null){
            criteria.and("siteCode").is(siteCode);
        }

        return mongoTemplate.find(new Query(criteria), DeviceEntity.class, uniqueCode + CollectionSuffix.DEVICE);
    }
}
