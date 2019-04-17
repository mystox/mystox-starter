package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.config.MongoConfig;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fengw
 * 设备列表数据库操作
 * 新建文件 2019-4-16 18:38:12
 */
@Component
public class DeviceDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 获取指定FSU下的所有设备信息
     * @param fsuId fsuId
     * @return 设备信息列表
     */
    public List<JsonDevice> getListByFsuId(String fsuId) {
        Criteria criteria = Criteria.where("fsuId").is(fsuId);
        return mongoTemplate.find(Query.query(criteria),
                JsonDevice.class, MongoConfig.T_DEVICE_LIST);
    }

    /**
     * 添加设备信息
     * @param list 待添加的设备信息列表
     */
    public void insertListByFsuId(List<JsonDevice> list) {
        mongoTemplate.insert(list, MongoConfig.T_DEVICE_LIST);
    }

    /**
     * 删除指定FSU下的所有设备信息
     * @param fsuId fsuId
     * @return 是否删除成功
     */
    public boolean deleteListByFsuId(String fsuId) {
        Criteria criteria = Criteria.where("fsuId").is(fsuId);
        WriteResult writeResult = mongoTemplate.remove(Query.query(criteria),
                MongoConfig.T_DEVICE_LIST);
        //由于存在FSU下无设备的情况导致writeResult.getN()为0
        //所以无法准确判断是否删除成功
        return true;
    }
}
