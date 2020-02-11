package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.DeviceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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

    /**
     * 修改信号类型映射表
     */
    public void modifyTypeList(String uniqueCode, List<DeviceType> deviceTypes) {
        mongoTemplate.dropCollection(uniqueCode + CollectionSuffix.SIGNAL_TYPE);
        for (DeviceType deviceType : deviceTypes) {
            mongoTemplate.save(deviceType, uniqueCode + CollectionSuffix.SIGNAL_TYPE);
        }
    }
}
