package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.MaintainerEntity;
import com.kongtrolink.framework.scloud.entity.model.MaintainerModel;
import com.kongtrolink.framework.scloud.query.MaintainerQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private String table = CollectionSuffix.MAINTAINER;
    //用户站点权限关联查询别名字段
    private String user_site_info = "user_site_info";
    private String user_site_userId = "userId";

    /**
     * @param uniqueCode
     * @param maintainerQuery
     * @auther: liudd
     * @date: 2020/4/21 8:41
     * 功能描述:获取列表
     */
    public List<MaintainerEntity> list(String uniqueCode, MaintainerQuery maintainerQuery) {
        Criteria criteria = new Criteria();
        String username = maintainerQuery.getUsername();
        if(!StringUtil.isNUll(username)){
            username = MongoRegexUtil.escapeExprSpecialWord(username);
            criteria.and("username").regex(".*?" + username + ".*?");
        }
        String companyName = maintainerQuery.getCompanyName();
        if (!StringUtil.isNUll(companyName)){
            companyName = MongoRegexUtil.escapeExprSpecialWord(companyName);
            criteria.and("companyName").regex(".*?" + companyName + ".*?");
        }
        String siteCode = maintainerQuery.getSiteCode();
        if(StringUtil.isNUll(siteCode)){
            return mongoTemplate.find(new Query(criteria), MaintainerEntity.class, uniqueCode + CollectionSuffix.MAINTAINER);
        }else{
            //使用关联查询
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.lookup(uniqueCode+CollectionSuffix.USER_SITE, user_site_userId, user_site_userId, user_site_info),
                    Aggregation.match(Criteria.where("user_site_info.siteCode").is(maintainerQuery.getSiteCode()))
            );
            AggregationResults<MaintainerEntity> aggregate = mongoTemplate.aggregate(aggregation, uniqueCode + table, MaintainerEntity.class);
            return aggregate.getMappedResults();
        }
    }

    /**
     * 根据查询条件，获取维护人员列表
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

    /**
     * 保存维护人员
     */
    public void saveMaintainer(String uniqueCode, MaintainerEntity maintainerEntity){
        mongoTemplate.save(maintainerEntity, uniqueCode + CollectionSuffix.MAINTAINER);
    }

    /**
     * 修改维护人员(扩展信息)
     */
    public boolean modifyMaintainer(String uniqueCode, MaintainerModel maintainerModel){
        Criteria criteria = Criteria.where("userId").is(maintainerModel.getUserId());

        Update update = new Update();
        update.set("companyName", maintainerModel.getCompanyName());
        update.set("organizationId", maintainerModel.getOrganizationId());
        update.set("status", maintainerModel.getStatus());
        update.set("hireDate", maintainerModel.getHireDate());
        update.set("expireDate", maintainerModel.getExpireDate());
        update.set("major", maintainerModel.getMajor());
        update.set("skill", maintainerModel.getSkill());
        update.set("address", maintainerModel.getAddress());
        update.set("idCard", maintainerModel.getIdCard());
        update.set("duty", maintainerModel.getDuty());
        update.set("education", maintainerModel.getEducation());
        update.set("authentication", maintainerModel.getAuthentication());
        update.set("authLevel", maintainerModel.getAuthLevel());
        update.set("authDate", maintainerModel.getAuthDate());
        update.set("authExpireDate", maintainerModel.getAuthExpireDate());

        WriteResult result = mongoTemplate.updateFirst(new Query(criteria), update, uniqueCode + CollectionSuffix.MAINTAINER);
        return result.getN()>0;
    }

    /**
     * 删除维护人员
     */
    public void deleteMaintainer(String uniqueCode, String userId){
        Criteria criteria = Criteria.where("userId").is(userId);
        mongoTemplate.remove(new Query(criteria), MaintainerEntity.class, uniqueCode + CollectionSuffix.MAINTAINER);
    }

    /**
     * 根据账号，查找维护人员
     */
    public MaintainerEntity findMaintainerByUsername(String uniqueCode, String username){
        Criteria criteria = Criteria.where("username").is(username);
        return mongoTemplate.findOne(new Query(criteria), MaintainerEntity.class, uniqueCode + CollectionSuffix.MAINTAINER);
    }
}
