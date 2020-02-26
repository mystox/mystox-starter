package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 站点相关数据查询类
 * Created by Eric on 2020/2/12.
 */
@Repository
public class SiteMongo {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 添加站点
     */
    public void addSite(String uniqueCode, SiteEntity siteEntity){
        mongoTemplate.save(siteEntity, uniqueCode + CollectionSuffix.SITE);
    }

    /**
     * 统计区域下站点数量
     */
    public Integer countSiteInTier(String uniqueCode, String tierCode){
        Criteria criteria = Criteria.where("tierCode").is(tierCode);

        return (int)mongoTemplate.count(new Query(criteria), SiteEntity.class, uniqueCode + CollectionSuffix.SITE);
    }

    /**
     * 通过站点编码查询站点
     */
    public SiteEntity findSiteByCode(String uniqueCode, String siteCode) {
        return mongoTemplate.findOne(
                new Query(Criteria.where("code").is(siteCode)),
                SiteEntity.class, uniqueCode + CollectionSuffix.SITE);
    }

    /**
     * 查询获取站点列表
     */
    public List<SiteEntity> findSiteList(String uniqueCode, SiteQuery siteQuery){
        String address = siteQuery.getAddress(); //站点地址
        String towerType = siteQuery.getTowerType();   //铁塔类型
        String respName = siteQuery.getRespName();    //资产管理员名称
        Long startTime = siteQuery.getStartTime(); //开始时间
        Long endTime = siteQuery.getEndTime();   //结束时间
        String siteCode = siteQuery.getSiteCode();  //站点编码（模糊搜索）
        List<String> siteCodes = siteQuery.getSiteCodes(); //站点编码集合(资管获取的)

        Criteria criteria = new Criteria();
        if (towerType != null){
            criteria.and("towerType").is(towerType);
        }
        if (siteCode != null && !siteCode.equals("")){
            address = MongoRegexUtil.escapeExprSpecialWord(address);
            criteria.and("address").regex("^" + address + ".*?");
        }
        if (siteCodes != null){
            criteria.and("code").in(siteCodes);
        }
        if (address != null && !address.equals("")){
            address = MongoRegexUtil.escapeExprSpecialWord(address);
            criteria.and("address").regex(".*?" + address + ".*?");
        }
        if (respName != null && !respName.equals("")){
            respName = MongoRegexUtil.escapeExprSpecialWord(respName);
            criteria.and("respName").regex(".*?" + respName + ".*?");
        }
        if (startTime != null){
            if (endTime != null){
                criteria.and("createTime").gte(new Date(startTime)).lte(new Date(endTime));
            }else {
                criteria.and("createTime").gte(new Date(startTime));
            }
        }else {
            if (endTime != null){
                criteria.and("createTime").lte(new Date(endTime));
            }
        }

        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "createTime"));

        return mongoTemplate.find(query, SiteEntity.class, uniqueCode + CollectionSuffix.SITE);
    }
}
