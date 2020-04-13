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
    public List<UserSiteEntity> findUserSite(String uniqueCode, String userId){
        Criteria criteria = Criteria.where("userId").is(userId);
        return mongoTemplate.find(new Query(criteria), UserSiteEntity.class, uniqueCode + CollectionSuffix.USER_SITE);
    }

    /**
     * 保存用户管辖站点
     */
    public void saveUserSite(String uniqueCode, List<UserSiteEntity> userSites){
        mongoTemplate.insert(userSites, uniqueCode + CollectionSuffix.USER_SITE);
    }

    /**
     * 删除用户所有管辖站点
     */
    public void deleteUserSite(String uniqueCode, String userId){
        Criteria criteria = Criteria.where("userId").is(userId);
        mongoTemplate.remove(new Query(criteria), UserSiteEntity.class, uniqueCode + CollectionSuffix.USER_SITE);
    }

    /**
     * 从用户的管辖站点中删除该站点
     */
    public void deleteSitesFromUserSite(String uniqueCode, List<String> siteCodes){
        Criteria criteria = Criteria.where("siteCode").in(siteCodes);
        mongoTemplate.remove(new Query(criteria), UserSiteEntity.class, uniqueCode + CollectionSuffix.USER_SITE);
    }
}
