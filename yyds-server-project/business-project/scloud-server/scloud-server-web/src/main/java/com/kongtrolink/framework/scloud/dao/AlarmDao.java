package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.Alarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 14:56
 * @Description:
 */
@Repository
public class AlarmDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public void add(String uniqueCode, String table, Alarm alarm){

    }


}
