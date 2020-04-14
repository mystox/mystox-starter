package com.kongtrolink.framework.scloud.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 工程管理 相关数据操作类
 * Created by Eric on 2020/4/13.
 */
@Repository
public class ProjectMongo {

    @Autowired
    MongoTemplate mongoTemplate;


}
