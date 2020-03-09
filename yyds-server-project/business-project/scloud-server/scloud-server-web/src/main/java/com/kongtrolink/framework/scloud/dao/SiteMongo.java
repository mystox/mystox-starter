package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.util.MongoRegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    /**
     * 修改站点
     */
    public void modifySite(String uniqueCode, SiteModel siteModel){
        String siteCode = siteModel.getCode(); //站点编码
        String coordinate = siteModel.getCoordinate();	//站点经纬度
        String address = siteModel.getAddress();	//站点地址
        String respName = siteModel.getRespName();	//资产管理员名称
        String respPhone = siteModel.getRespPhone();	//联系电话
        String towerHeight = siteModel.getTowerHeight();//铁塔高度
        String towerType = siteModel.getTowerType();//铁塔类型
        String shareInfo = siteModel.getShareInfo();//共享信息
        String assetNature = siteModel.getAssetNature();	//产权性质（自建、社会资源、注入）
        String areaCovered = siteModel.getAreaCovered();	//占地面积
        int fileId = siteModel.getFileId();	//站点图纸文件Id

        Criteria criteria = Criteria.where("code").is(siteCode);
        Update update = new Update();
        update.set("address", address);
        update.set("respName", respName);
        update.set("respPhone", respPhone);
        update.set("towerHeight", towerHeight);
        update.set("towerType", towerType);
        update.set("shareInfo", shareInfo);
        update.set("assetNature", assetNature);
        update.set("areaCovered", areaCovered);
        update.set("fileId", fileId);
        if (coordinate != null){
            update.set("coordinate", coordinate);
        }

        mongoTemplate.updateFirst(new Query(criteria), update, SiteEntity.class, uniqueCode + CollectionSuffix.SITE);
    }

    /**
     * （批量）删除站点
     */
    public void deleteSites(String uniqueCode, SiteQuery siteQuery){
        List<String> siteCodes = siteQuery.getSiteCodes();

        Criteria criteria = new Criteria();
        if (siteCodes != null && siteCodes.size() > 0){
            criteria.and("code").in(siteCodes);
        }
        mongoTemplate.remove(new Query(criteria), SiteEntity.class, uniqueCode + CollectionSuffix.SITE);
    }

}
