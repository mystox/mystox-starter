package com.kongtrolink.yyjw.dao;

import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2018/12/24, 16:42.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class FsuDevicesDao
{
    private static final String tableName = "tb_cntb_fsu_device";
    @Autowired
    MongoTemplate mongoTemplate;


    public Map<String, Integer> getFsuDeviceCount(List<String> fsuIds)
    {

        Map<String, Integer> fsuDeviceCountMap = new HashMap<>();
        AggregationResults results = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(Criteria.where("fsuId").in(fsuIds)),
                Aggregation.project("fsuId").and("devices").size().as("deviceCount")), tableName, DBObject.class);
        List<DBObject> dbObjects = results.getMappedResults();
        if (dbObjects != null && dbObjects.size() > 0)
        {
            for (DBObject dbObject : dbObjects)
            {
                String fsuId = (String) dbObject.get("fsuId");
                Integer count = (Integer) dbObject.get("deviceCount");
                fsuDeviceCountMap.put(fsuId, count);

            }

        }
        return fsuDeviceCountMap;

    }

}
