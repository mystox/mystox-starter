package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.ProjectOrderEntity;
import com.kongtrolink.framework.scloud.entity.ProjectOrderLogEntity;
import com.kongtrolink.framework.scloud.query.ProjectOrderQuery;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工程管理 相关数据操作类
 * Created by Eric on 2020/4/13.
 */
@Repository
public class ProjectMongo {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 保存测试单
     */
    public void saveProjectOrder(String uniqueCode, ProjectOrderEntity projectOrderEntity){
        mongoTemplate.save(projectOrderEntity, uniqueCode + CollectionSuffix.PROJECT_ORDER);
    }

    /**
     * 统计测试单数量
     */
    public int countProjectOrder(String uniqueCode, ProjectOrderQuery projectOrderQuery){
        Criteria criteria = createBaseCriteria(projectOrderQuery);
        Query query = new Query(criteria);
        return (int)mongoTemplate.count(query, ProjectOrderEntity.class, uniqueCode + CollectionSuffix.PROJECT_ORDER);
    }

    /**
     * 查询获取测试单（分页）
     */
    public List<ProjectOrderEntity> findProjectOrders(String uniqueCode, ProjectOrderQuery projectOrderQuery){
        int currentPage = projectOrderQuery.getCurrentPage();
        int pageSize = projectOrderQuery.getPageSize();

        Criteria criteria = createBaseCriteria(projectOrderQuery);

        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "applyTime"));
        query.skip((currentPage - 1) * pageSize).limit(pageSize);
        return mongoTemplate.find(query, ProjectOrderEntity.class, uniqueCode + CollectionSuffix.PROJECT_ORDER);
    }

    private Criteria createBaseCriteria(ProjectOrderQuery projectOrderQuery){
        Long startTime = projectOrderQuery.getStartTime(); //开始时间
        Long endTime = projectOrderQuery.getEndTime();   //结束时间

        Criteria criteria = new Criteria();
        if (projectOrderQuery.getFsuCodes() != null){
            criteria.and("fsuCode").in(projectOrderQuery.getFsuCodes());
        }
        if (projectOrderQuery.getSiteCodes() != null){
            criteria.and("siteCode").in(projectOrderQuery.getSiteCodes());
        }
        if (!StringUtil.isNUll(projectOrderQuery.getState())){
            criteria.and("state").is(projectOrderQuery.getState());
        }

        if (startTime != null){
            if (endTime == null){
                criteria.and("applyTime").gte(startTime);
            }else {
                criteria.and("applyTime").gte(startTime).lte(endTime);
            }
        }else {
            if (endTime != null){
                criteria.and("applyTime").lte(endTime);
            }
        }

        return criteria;
    }

    /**
     * 保存测试单操作记录
     */
    public void saveProjectOrderLog(String uniqueCode, ProjectOrderLogEntity projectOrderLogEntity){
        mongoTemplate.save(projectOrderLogEntity, uniqueCode + CollectionSuffix.PROJECT_ORDER_LOG);
    }

    /**
     * 查询获取测试单操作记录
     */
    public List<ProjectOrderLogEntity> findProjectOrderLogs(String uniqueCode, ProjectOrderQuery projectOrderQuery){
        Criteria criteria = Criteria.where("orderId").is(projectOrderQuery.getOrderId());
        return mongoTemplate.find(new Query(criteria), ProjectOrderLogEntity.class, uniqueCode + CollectionSuffix.PROJECT_ORDER_LOG);
    }
}
