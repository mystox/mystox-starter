package com.kongtrolink.framework.scloud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 设备资产 相关数据查询类
 * Created by Eric on 2020/2/12.
 */
@Repository
public class DeviceMongo {

    @Autowired
    MongoTemplate mongoTemplate;


}
