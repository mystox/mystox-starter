package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.UserEntity;
import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.query.UserQuery;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

    public UserModel findUserById(String uniqueCode, String userId) {
       return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), UserModel.class, uniqueCode + CollectionSuffix.USER);

    }
    /**
     * 添加系统用户
     */
    public void addUser(String uniqueCode, UserEntity userEntity){
        mongoTemplate.save(userEntity,uniqueCode+ CollectionSuffix.USER);
    }

    /**
     * 修改系统用户
     */
    public boolean modifyUser(String uniqueCode, UserModel userModel){
        String userId = userModel.getUserId();
        Criteria criteria = Criteria.where("userId").is(userId);
        Update update = new Update();
        update.set("userStatus",userModel.getUserStatus());
        update.set("validTime",userModel.getValidTime());
        if (userModel.getValidTime()!=null){
            update.set("validTime","临时");
        }
        update.set("workId",userModel.getWorkId());
        update.set("remark",userModel.getRemark());
        update.set("gender",userModel.getGender());
        WriteResult result = mongoTemplate.updateFirst(new Query(criteria),update,uniqueCode+CollectionSuffix.USER);
        return result.getN()>0;

    }

    /**
     * 删除系统用户
     */
    public void deleteUser(String uniqueCode, UserModel userModel){
        Criteria criteria = Criteria.where("userId").is(userModel.getUserId());
        mongoTemplate.remove(new Query(criteria),UserModel.class,uniqueCode+CollectionSuffix.USER);
    }

    /**
     * 用户列表
     */
    public UserEntity listUser(String uniqueCode, String id, UserQuery userQuery){
        Criteria criteria = Criteria.where("userId").is(id);
        Query query = new Query(criteria);
        UserEntity user = mongoTemplate.findOne(query,UserEntity.class,uniqueCode+CollectionSuffix.USER);
        return user;
    }
    /**
     * 批量添加用户
     */
    public void addUserBatch(String uniqueCode,List<UserEntity> list){
        mongoTemplate.insert(list,uniqueCode+CollectionSuffix.USER);
    }

}
