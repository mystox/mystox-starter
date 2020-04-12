package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.base.CustomOperation;
import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.constant.FsuOperationState;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.home.*;
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
        DBObject lookupSql = getLookupSqlId(uniqueCode,userId,"siteId");
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
     * 首页 - FSU在线情况
     * @param uniqueCode 企业编码
     * @param userId   站点ID
     * @param homeQuery  区域信息
     * @return 返回
     */
    public List<HomeFsuOnlineInfo> getHomeFsuStateList(String uniqueCode,String userId, HomeQuery homeQuery) {
        //查询条件
        String code = homeQuery.getTierCode();
        Criteria criteria = Criteria.where("typeCode").is("038");
        if (code != null && !"".equals(code)) {
            criteria.and("code").regex("^"+code);//模糊查询
        }
        DBObject lookupSql = getLookupSqlId(uniqueCode,userId,"siteId");
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                new CustomOperation(lookupSql), //取得字段()
                match(new Criteria("stockData.userId").exists(true)),
                project("id","siteCode","code","state")
        );

        AggregationResults<HomeFsuOnlineInfo> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.DEVICE, HomeFsuOnlineInfo.class);
        return result.getMappedResults();
    }
    /**
     * 根据区域 用户权限获取站点列表
     * @param uniqueCode 企业编码
     * @param userId 用户ID
     * @param homeQuery 区域
     * @return 站点总数
     */
    public List<HomeSiteAlarmMap> getHomeSiteList(String uniqueCode,String userId,HomeQuery homeQuery) {
        //查询条件
        String code = homeQuery.getTierCode();
        Criteria criteria = new Criteria();
        if (code != null && !"".equals(code)) {
            criteria.and("code").regex("^"+code);//模糊查询
        }
        DBObject lookupSql = getLookupSqlId(uniqueCode,userId,"id");
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                new CustomOperation(lookupSql), //取得字段()
                match(new Criteria("stockData.userId").exists(true))
        );
        AggregationResults<HomeSiteAlarmMap> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.SITE, HomeSiteAlarmMap.class);
        return result.getMappedResults();
    }
    /**
     * 根据区域 用户权限获取  交维态 站点列表
     * @param uniqueCode 企业编码
     * @param userId 用户ID
     * @param homeQuery 区域
     * @return 站点总数
     */
    public List<HomeSiteInfo> getOperationSiteList(String uniqueCode,String userId, HomeQuery homeQuery) {
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
                "siteCode",1
        ).append(
                "operationState",1
        ).append(
                "checkState", new BasicDBObject(
                        "$cond", new Object[]{new BasicDBObject("$eq", new Object[]{ "$operationState", "交维态"}), 1, 0}
                )
        )
        );
        DBObject lookupSql = getLookupSqlId(uniqueCode,userId,"siteId");
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                new CustomOperation(projectSql), //取得字段
                new CustomOperation(lookupSql), //取得字段()
                match(new Criteria("stockData.userId").exists(true)),
                group("siteCode").min("checkState").as("siteState"),//group
                match(new Criteria("siteState").is(1)),//只查询交维态的
                project().and("siteCode").previousOperation()
        );
        AggregationResults<HomeSiteInfo> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.DEVICE, HomeSiteInfo.class);
        return result.getMappedResults();
    }

    public List<HomeReportModel> getHomeAlarmLevelNum(String uniqueCode,List<String> siteCodes) {
        Criteria criteria = Criteria.where("state").is("待处理").and("siteCode").in(siteCodes);
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                group("level","checkState").count().as("count"),
                project("count").and("_id.level").as("state").and("_id.checkState").as("checkState")
        );
        AggregationResults<HomeReportModel> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.CUR_ALARM_BUSINESS, HomeReportModel.class);
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
        Criteria criteria = getHomeWorkQuery(homeQuery,false);
        DBObject lookupSql = getLookupSqlCode(uniqueCode,userId,"site.strId");
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

    public int getHomeWorkModelOverTime(String uniqueCode, String userId, HomeQuery homeQuery) {
        //查询条件
        Criteria criteria = getHomeWorkQuery(homeQuery,true);
        DBObject lookupSql = getLookupSqlCode(uniqueCode,userId,"site.strId");
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                new CustomOperation(lookupSql), //取得字段()
                match(new Criteria("stockData.userId").exists(true))
        );
        AggregationResults<HomeWorkModel> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.WORK, HomeWorkModel.class);
        return result.getMappedResults().size();
    }

    private Criteria getHomeWorkQuery(HomeQuery homeQuery,boolean isOverTime){
        String code = homeQuery.getTierCode();
        Date startTime = homeQuery.getStartTime();//开始时间
        Date endTime = homeQuery.getEndTime(); //结束时间
        Criteria criteria = Criteria.where("sentTime").gte(startTime).lte(endTime);
        if(isOverTime){
            criteria.and("isOverTime").is("是");
        }
        if (code != null && !"".equals(code)) {
            criteria.and("site.strId").regex("^"+code);//模糊查询
        }
        return criteria;
    }
    /**
     * 告警工单统计
     *
     * @param uniqueCode 企业编码
     * @param userId     用户ID
     * @param homeQuery  区域
     * @return 站点总数
     */
    public List<HomeReportModel> getHomeReportModel(String uniqueCode, String userId, HomeQuery homeQuery) {
        //查询条件
        String code = homeQuery.getTierCode();
        Criteria criteria = new Criteria();
        if (code != null && !"".equals(code)) {
            criteria.and("siteCode").regex("^"+code);//模糊查询
        }
        DBObject lookupSql = getLookupSqlCode(uniqueCode,userId,"siteCode");
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                new CustomOperation(lookupSql), //取得字段()
                match(new Criteria("stockData.userId").exists(true)),
                group("siteCode","level").count().as("count"),
                project("count").and("_id.siteCode").as("siteCode").and("_id.level").as("level")
        );
        AggregationResults<HomeReportModel> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.CUR_ALARM_BUSINESS, HomeReportModel.class);
        return result.getMappedResults();
    }
    /**
     * FSU在线状态统计 交维态FSU设备的实时在线情况百分比
     *
     * @param uniqueCode 企业编码
     * @param userId     用户ID
     * @param homeQuery  区域
     * @return 站点总数
     */
    public List<HomeFsuOnlineModel> getHomeFsuOnlineModel(String uniqueCode, String userId, HomeQuery homeQuery) {
        //查询条件
        String code = homeQuery.getTierCode();
        Criteria criteria = Criteria.where("typeCode").is("038").and("operationState").is(FsuOperationState.MAINTENANCE);
        if (code != null && !"".equals(code)) {
            criteria.and("code").regex("^"+code);//模糊查询
        }
        DBObject lookupSql = getLookupSqlId(uniqueCode,userId,"siteId");
        Aggregation agg = Aggregation.newAggregation(
                match(criteria),
                new CustomOperation(lookupSql), //取得字段()
                match(new Criteria("stockData.userId").exists(true)),
                group("state").count().as("count"),
                project("count").and("state").previousOperation()
        );
        AggregationResults<HomeFsuOnlineModel> result = mongoTemplate.aggregate(agg,uniqueCode+ CollectionSuffix.DEVICE, HomeFsuOnlineModel.class);
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
    private DBObject getLookupSqlId(String uniqueCode,String userId,String localId){
        DBObject lookupSql = new BasicDBObject(
                "$lookup", new BasicDBObject(
                "from",uniqueCode+"_user_site"
        ).append(
                "let",new BasicDBObject(
                        "siteId","$"+localId
                )
        ).append(
                "pipeline", new Object[]{
                        new BasicDBObject("$match", new BasicDBObject("$expr",new BasicDBObject("$and",new BasicDBObject("$and",new Object[]{
                                new BasicDBObject("$eq", new Object[]{ "$userId", userId}),
                                new BasicDBObject("$eq", new Object[]{ "$siteId", "$$siteId"})
                        })))),
                        new BasicDBObject("$project",new BasicDBObject("userId",1))
                }
        ).append(
                "as", "stockData"
        )
        );
        return lookupSql;
    }
    private DBObject getLookupSqlCode(String uniqueCode,String userId,String localId){
        DBObject lookupSql = new BasicDBObject(
                "$lookup", new BasicDBObject(
                "from",uniqueCode+"_user_site"
        ).append(
                "let",new BasicDBObject(
                        "siteCode","$"+localId
                )
        ).append(
                "pipeline", new Object[]{
                        new BasicDBObject("$match", new BasicDBObject("$expr",new BasicDBObject("$and",new BasicDBObject("$and",new Object[]{
                                new BasicDBObject("$eq", new Object[]{ "$userId", userId}),
                                new BasicDBObject("$eq", new Object[]{ "$siteCode", "$$siteCode"})
                        })))),
                        new BasicDBObject("$project",new BasicDBObject("userId",1))}
        ).append(
                "as", "stockData"
        )
        );
        return lookupSql;
    }
}
