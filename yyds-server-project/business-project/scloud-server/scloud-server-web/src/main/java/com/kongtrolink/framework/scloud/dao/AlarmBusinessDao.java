package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 18:46
 * @Description:
 */
@Repository
public class AlarmBusinessDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public void add(String uniqueCode, String table, AlarmBusiness business) {
        mongoTemplate.save(business, uniqueCode + table );
    }

    public void add(String uniqueCode, String table, List<AlarmBusiness> businessList) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, uniqueCode + table );
        for(AlarmBusiness business : businessList){
            bulkOperations.insert(business);
        }
        bulkOperations.execute();
    }

    public List<AlarmBusiness> listByKeyList(String uniqueCode, String table, List<String> keyList) {
        Criteria criteria = Criteria.where("key").in(keyList);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, AlarmBusiness.class, uniqueCode + table);
    }
}
