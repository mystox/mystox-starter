package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.core.entity.Fsu;
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
 * \* @Author: mystox
 * \* Date: 2018/12/3 11:08
 * \* Description:
 * \
 */
@Component
public class FsuDao {
    @Autowired
    MongoTemplate mongoTemplate;
    private static final String tableName = "tb_cntb_fsu_info";

    public List<Fsu> getAllFsu() {
        return mongoTemplate.findAll(Fsu.class,tableName);
    }

    public List<Fsu> findByCoordinate(String[] coordinateArr) {
        return null;
    }

    public Fsu findByFsuId(String fsuId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("fsuId").is(fsuId)),Fsu.class,tableName);
    }
    public boolean existsByFsuId(String fsuId) {
        return mongoTemplate.exists(Query.query(Criteria.where("fsuId").is(fsuId)),tableName);
    }

    public List<Fsu> findByCondition(Map<String, Object> requestBody)
    {
        Criteria criteria = new Criteria();
        String imsi = (String) requestBody.get("imsi");
        String fsuId = (String) requestBody.get("fsu_id");
        String name = (String) requestBody.get("name");
        Integer pageSize = (Integer) requestBody.get("page_size");
        Integer curPage = (Integer) requestBody.get("cur_page");

        if (StringUtils.isNotBlank(imsi))
            criteria.and("imsi").is(imsi);
        if (StringUtils.isNotBlank(fsuId))
            criteria.and("fsuId").is(fsuId);
        if (StringUtils.isNotBlank(name))
            criteria.and("name").regex(name);

        Query query = Query.query(criteria);
        if (pageSize!=null&& curPage!=null)
            query.with(new PageRequest(curPage, pageSize));
        return mongoTemplate.find(query, Fsu.class, tableName);
//        return mongoTemplate.find();
    }

    public void saveFsu(Fsu fsu)
    {
        mongoTemplate.save(fsu,tableName);
    }
}