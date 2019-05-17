package com.kongtrolink.framework.execute.module.dao;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.MongoTableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/5/16, 15:14.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class CompilerDao {
    @Autowired
    MongoTemplate mongoTemplate;


    public Integer getBusinessSceneId(String adapterVer) {

        JSONObject businessScene = mongoTemplate.findOne(Query.query(Criteria.where("adapterVersion").in(adapterVer)), JSONObject.class, MongoTableName.COMPILER_BUSINESSSCENE_DIC);
        if (businessScene!=null)
        return businessScene.getInteger("businessSceneId");
        else return null;

    }

    public Integer getProductId(String rule) {

        JSONObject product = mongoTemplate.findOne(Query.query(Criteria.where("productRule").in(rule)), JSONObject.class, MongoTableName.COMPILER_PRODUCT_DIC);
        if (product != null)
        return product.getInteger("productId");
        else  return null;

    }
}
