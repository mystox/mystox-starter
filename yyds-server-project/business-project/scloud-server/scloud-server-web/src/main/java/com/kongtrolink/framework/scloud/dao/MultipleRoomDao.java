package com.kongtrolink.framework.scloud.dao;


import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.multRoom.RoomSignalTypeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;



/**
 * 综合机房
 * by Mag on 6/21/2018.
 */
@Repository
public class MultipleRoomDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 保存自定义信号点
     *
     * @param uniqueCode 企业编码
     * @param config 信号点信息
     */
    public void addShowSignalConfig(String uniqueCode, RoomSignalTypeConfig config) {
         mongoTemplate.save(config,uniqueCode+ CollectionSuffix.MULTIPLE_ROOM_SIGNAL_CONFIG);
    }

    public  void delShowSignalConfig(String uniqueCode,String deviceId){
        Criteria criteria = Criteria.where("deviceId").is(deviceId);
        Query query = new Query(criteria);
        mongoTemplate.remove(query,uniqueCode+CollectionSuffix.MULTIPLE_ROOM_SIGNAL_CONFIG);
    }
    /**
     * 根据设备查询信号点配置
     *
     * @param uniqueCode 企业编码
     * @param deviceId 信号点信息
     */
    public RoomSignalTypeConfig queryRoomSignalTypeConfig(String uniqueCode, int deviceId) {
        Criteria criteria = Criteria.where("deviceId").is(deviceId);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query,RoomSignalTypeConfig.class,uniqueCode+CollectionSuffix.MULTIPLE_ROOM_SIGNAL_CONFIG);
    }

}
