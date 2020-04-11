package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统用户 相关数据操作类
 * Created by Eric on 2020/2/28.
 */
@Repository
public class UserMongo {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 获取用户管辖站点
     */
    public List<UserSiteEntity> findUserSite(String uniqueCode, UserSiteEntity userSiteEntity){
        Criteria criteria = Criteria.where("userId").is(userSiteEntity.getUserId());
        return mongoTemplate.find(new Query(criteria), UserSiteEntity.class, uniqueCode + CollectionSuffix.USER_SITE);
    }

    /**
     * 保存或修改用户管辖站点
     */
    public void upsertUserSite(String uniqueCode, UserSiteEntity userSiteEntity){
        Criteria criteria = Criteria.where("userId").is(userSiteEntity.getUserId());

        Update update = new Update();
        update.set("siteCodes", userSiteEntity);

        mongoTemplate.upsert(new Query(criteria), update, UserSiteEntity.class, uniqueCode + CollectionSuffix.USER_SITE);
    }

    /**
     * 删除用户管辖站点
     */
    public void deleteUserSite(String uniqueCode, String userId){
        Criteria criteria = Criteria.where("userId").is(userId);
        mongoTemplate.remove(new Query(criteria), UserSiteEntity.class, uniqueCode + CollectionSuffix.USER_SITE);
    }
}
