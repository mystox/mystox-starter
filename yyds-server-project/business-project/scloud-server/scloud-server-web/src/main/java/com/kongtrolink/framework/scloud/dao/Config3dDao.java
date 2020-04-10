/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.framework.scloud.dao;


import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.config3d.Config3dScene;
import com.kongtrolink.framework.scloud.entity.config3d.ConfigAppLocateMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * copy from scloud
 *  组态功能
 * @author Mag
 */
@Repository
public class Config3dDao {
    
    @Autowired
    MongoTemplate mongoTemplate;
    
    public void upsert(String uniqueCode, Config3dScene config3dScene) {
        mongoTemplate.remove(
                new Query(Criteria.where("siteId").is(config3dScene.getSiteId())),
                uniqueCode + CollectionSuffix.CONFIGRATION_3D);
        mongoTemplate.save(config3dScene, 
                uniqueCode + CollectionSuffix.CONFIGRATION_3D);
    }
    
    public Config3dScene find(String uniqueCode, int siteId) {
        return mongoTemplate.findOne(new Query(Criteria.where("siteId").is(siteId)),
                Config3dScene.class, uniqueCode + CollectionSuffix.CONFIGRATION_3D);
    }

    public void saveConfigAppLocateMap(String uniqueCode, ConfigAppLocateMap locateMap) {
        mongoTemplate.save(locateMap, uniqueCode + CollectionSuffix.CONFIGRATION_APP);
    }

    public ConfigAppLocateMap findConfigAppLocateMap(String uniqueCode, int siteId) {
        Criteria criteria = Criteria.where("siteId").is(siteId);
        return mongoTemplate.findOne(
                new Query(criteria),
                ConfigAppLocateMap.class,
                uniqueCode + CollectionSuffix.CONFIGRATION_APP);
    }

    public void upsertConfigAppLocateMap(String uniqueCode, ConfigAppLocateMap locateMap) {
        Criteria criteria = Criteria.where("siteId").is(locateMap.getSiteId());
        Update update = new Update();
        update.set("devices", locateMap.getDevices());
        update.set("rooms", locateMap.getRooms());
        mongoTemplate.upsert(
                new Query(criteria), update,
                ConfigAppLocateMap.class,
                uniqueCode + CollectionSuffix.CONFIGRATION_APP);
    }

    public void removeConfigAppLocateMap(String uniqueCode, int siteId) {
        Criteria criteria = Criteria.where("siteId").is(siteId);
        mongoTemplate.remove(
                new Query(criteria),
                ConfigAppLocateMap.class,
                uniqueCode + CollectionSuffix.CONFIGRATION_APP);
    }
}
