package com.kongtrolink.framework.scloud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 系统用户 相关数据操作类
 * Created by Eric on 2020/2/28.
 */
@Repository
public class UserMongo {

    @Autowired
    MongoTemplate mongoTemplate;


}