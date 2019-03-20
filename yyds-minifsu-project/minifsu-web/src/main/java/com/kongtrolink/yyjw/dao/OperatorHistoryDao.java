package com.kongtrolink.yyjw.dao;

import com.kongtrolink.yyjw.model.OperatHistory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2018/12/24, 13:09.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class OperatorHistoryDao
{
    @Autowired
    MongoTemplate mongoTemplate;

    private static final String tableName = "tb_cntb_fsu_op";


    public List<OperatHistory> findByCondition(Map<String,Object> condition, String fsuId)
    {



        Integer pageSize = (Integer) condition.get("page_size");
        Integer curPage = (Integer) condition.get("cur_page");
        Long sTime = System.currentTimeMillis()/1000 - 7*24*3600;
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(fsuId))
        {
            criteria.and("fsuId").is(fsuId);
        }
        criteria.and("opTime").gte(sTime).lte(System.currentTimeMillis()/1000);
        Query query = Query.query(criteria);
        query.with(new PageRequest(curPage, pageSize));
        return mongoTemplate.find(query, OperatHistory.class, tableName);

    }
}
