package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.UserEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.query.UserQuery;
import com.mongodb.WriteResult;
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
        update.set("lockStatus",userModel.getLockStatus());
        update.set("userStatus",userModel.getUserStatus());
        update.set("validTime",userModel.getValidTime());
        update.set("workId",userModel.getWorkId());
        update.set("changeTime",userModel.getChangeTime());
        update.set("remark",userModel.getRemark());
        update.set("password",userModel.getPassword());
        update.set("sex",userModel.getSex());
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
    public UserModel listUser(String uniqueCode, String id, UserQuery userQuery){
        UserEntity userEntity = new UserEntity();
        String lockStatus = userEntity.getLockStatus(); //锁定状态
        String userStatus = userEntity.getUserStatus(); //用户状态
        Long validTime = userEntity.getValidTime();  //有效日期
        Criteria criteria = Criteria.where("userId").is(id);
        Criteria criteria1 = new Criteria();
        if (lockStatus != null && !lockStatus.equals("")){
            criteria1.and("lockStatus").is(lockStatus);
        }
        if (userStatus != null && !userStatus.equals("")){
            criteria1.and("userStatus").is(userStatus);
        }
        if (validTime != null){
            criteria1.and("validTime").gte(userQuery.getStartTime()).lte(userQuery.getEndTime());
        }
        criteria.andOperator(criteria1);
        Query query = new Query(criteria);
        UserModel user = (UserModel) mongoTemplate.find(query,UserModel.class,uniqueCode+CollectionSuffix.USER);
        return user;
    }
}
