package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.MaintainerEntity;
import com.kongtrolink.framework.scloud.query.MaintainerQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 维护用户 相关数据操作类
 * Created by Eric on 2020/2/28.
 */
@Repository
public class MaintainerMongo {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 根据查询条件，获取列表
     */
    public List<MaintainerEntity> findMaintainerList(String uniqueCode, MaintainerQuery maintainerQuery){
        String username = maintainerQuery.getUsername();    //账号
        String companyName = maintainerQuery.getCompanyName();  //代维公司

        Criteria criteria = new Criteria();
        if (!StringUtil.isNUll(username)){
            username = MongoRegexUtil.escapeExprSpecialWord(username);
            criteria.and("username").regex(".*?" + username + ".*?");
        }
        if (!StringUtil.isNUll(companyName)){
            companyName = MongoRegexUtil.escapeExprSpecialWord(companyName);
            criteria.and("companyName").regex(".*?" + companyName + ".*?");
        }

        return mongoTemplate.find(new Query(criteria), MaintainerEntity.class, uniqueCode + CollectionSuffix.MAINTAINER);
    }
}
