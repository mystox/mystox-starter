package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.core.entity.MongoTableName;
import com.kongtrolink.framework.execute.module.model.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;

/**
 * Created by mystoxlol on 2019/4/1, 9:45.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class DeviceDao {
    @Autowired
    MongoTemplate mongoTemplate;


    public Device findDeviceByTypeResNoPort(String sn, Integer type, Integer resNo, Integer port) {
        return mongoTemplate.findOne(Query.query(
                Criteria.where(sn).is(sn)
                        .and("type").is(type)
                        .and("resNo").is(resNo)
                        .and("port").is(port)), Device.class, MongoTableName.DEVICE);
    }

    public void save(Device device) {
        mongoTemplate.save(device, MongoTableName.DEVICE);
    }

    public void saveBatch(List<Device> devices) {
        mongoTemplate.insert(devices, MongoTableName.DEVICE);
    }

    public List<Device> findDevicesBySn(String sn) {
        return mongoTemplate.find(Query.query(Criteria.where("SN").is(sn)), Device.class, MongoTableName.DEVICE);
    }

    public List<Device> findDevicesBySnAndValid(String sn) {
        return mongoTemplate.find(Query.query(Criteria.where("SN").is(sn).and("invalidTime").is(new Date(0L))), Device.class, MongoTableName.DEVICE);
    }
}
