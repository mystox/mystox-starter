package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.base.CustomOperation;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.home.HomeFsuNumber;
import com.kongtrolink.framework.scloud.entity.model.home.HomeQuery;
import com.kongtrolink.framework.scloud.entity.model.home.HomeWorkModel;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

/**
 * 首页处理
 * @author Mag
 **/
@Repository
public class HomePageMongo {
    @Autowired
    MongoTemplate mongoTemplate;
    /**
     * 首页 - 站点数量(交维态/所有)
     * @param uniqueCode 企业编码
     * @param userId   站点ID
     * @param homeQuery  区域信息
     * @return 返回
     */
    public HomeFsuNumber getHomeFsuNumber(String uniqueCode,String userId, HomeQuery homeQuery) {
        //查询条件
        String code = homeQuery.getTierCode();
        Criteria criteria = Criteria.where("typeCode").is("038");
        if (code != null && !"".equals(code)) {
            criteria.and("code").regex("^"+code);//模糊查询
        }
        DBObject projectSql = new BasicDBObject(
                "$project", new BasicDBObject(
                "siteId",1
        ).append(
                "operationState",1
        ).append(
                "checkState", new BasicDBObject(
                        "$cond", new Object[]{new BasicDBObject("$eq", new Object[]{ "$operationState", "交维态"}), 1, 0}
                )
        )
        );
        DBObject lookupSql = getLookupSql(userId,"siteId");
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                new CustomOperation(projectSql), //取得字段
                new CustomOperation(lookupSql), //取得字段()
                match(new Criteria("stockData.userId").exists(true)),
                group("siteId").min("checkState").as("siteState"),//group
                project("siteId","siteState"),
                group().sum("siteState").as("intersectionState")//group
        );

        AggregationResults<HomeFsuNumber> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.DEVICE, HomeFsuNumber.class);
        List<HomeFsuNumber> list = result.getMappedResults();
        HomeFsuNumber homeFsuNumber = new HomeFsuNumber();//以防sql查出来的 是空对象
        if(list!=null && list.size()>0){
            homeFsuNumber = list.get(0);
        }
        return homeFsuNumber;
    }

    /**
     * 根据区域 用户权限获取站点列表
     * @param uniqueCode 企业编码
     * @param userId 用户ID
     * @param homeQuery 区域
     * @return 站点总数
     */
    public List<SiteEntity> getHomeSiteList(String uniqueCode,String userId,HomeQuery homeQuery) {
        //查询条件
        String code = homeQuery.getTierCode();
        Criteria criteria = new Criteria();
        if (code != null && !"".equals(code)) {
            criteria.and("code").regex("^"+code);//模糊查询
        }
        DBObject lookupSql = getLookupSql(userId,"id");
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                new CustomOperation(lookupSql), //取得字段()
                match(new Criteria("stockData.userId").exists(true))
        );
        AggregationResults<SiteEntity> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.SITE, SiteEntity.class);
        return result.getMappedResults();
    }

    /**
     * 告警工单统计
     *
     * @param uniqueCode 企业编码
     * @param userId     用户ID
     * @param homeQuery  区域
     * @return 站点总数
     */
    public List<HomeWorkModel> getHomeWorkModel(String uniqueCode, String userId, HomeQuery homeQuery) {
        //查询条件
        String code = homeQuery.getTierCode();
        Date startTime = homeQuery.getStartTime();//开始时间
        Date endTime = homeQuery.getEndTime(); //结束时间
        Criteria criteria = Criteria.where("sentTime").gte(endTime).lte(startTime);
        if (code != null && !"".equals(code)) {
            criteria.and("code").regex("^"+code);//模糊查询
        }
        DBObject lookupSql = getLookupSql(userId,"site.id");
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                new CustomOperation(lookupSql), //取得字段()
                match(new Criteria("stockData.userId").exists(true)),
                group("state").count().as("count"),
                project("count").and("state").previousOperation()
        );
        AggregationResults<HomeWorkModel> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.WORK, HomeWorkModel.class);
        return result.getMappedResults();
    }

    /**
     * 获取lookup 权限
     * @param userId 用户ID
     * @param localId 源数据保存站点的主键ID
     *                如：site 的 id
     *                   Device表的 siteId
     *                   work表的 site.id
     * @return sql
     */
    private DBObject getLookupSql(String userId,String localId){
        DBObject lookupSql = new BasicDBObject(
                "$lookup", new BasicDBObject(
                "from","YYTD_user_site"
        ).append(
                "let",new BasicDBObject(
                        "siteId","$"+localId
                )
        ).append(
                "pipeline", new Object[]{
                        new BasicDBObject("$match", new BasicDBObject("$expr",new BasicDBObject("$and",new BasicDBObject("$and",new Object[]{
                                new BasicDBObject("$eq", new Object[]{ "$userId", userId}),
                                new BasicDBObject("$eq", new Object[]{ "$siteId", "$$siteId"})
                        }))))}
        ).append(
                "as", "stockData"
        )
        );
        return lookupSql;
    }
}
