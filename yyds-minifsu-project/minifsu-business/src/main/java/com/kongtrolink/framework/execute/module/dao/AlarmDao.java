package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.core.entity.Alarm;
import com.kongtrolink.framework.core.entity.MongoTableName;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/4/1 20:28
 * @Description:
 */
@Repository
public class AlarmDao {

    @Autowired
    MongoTemplate mongoTemplate;
    private String tableName = MongoTableName.ALARM;

    /**
     * @auther: liudd
     * @date: 2019/4/1 20:33
     * 功能描述:保存历史告警
     */
    public boolean save(List<Alarm> alarmList){
        mongoTemplate.insert(alarmList, tableName);
        return true;
    }

}
