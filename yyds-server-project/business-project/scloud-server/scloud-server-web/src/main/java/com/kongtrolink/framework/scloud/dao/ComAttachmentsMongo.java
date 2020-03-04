package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.ComAttachmentsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * 附件管理 数据查询类
 * Created by Eric on 2020/3/3.
 */
@Repository
public class ComAttachmentsMongo {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 保存附件
     */
    public void saveComAttachmentsEntity(String uniqueCode, ComAttachmentsEntity entity) {
        mongoTemplate.save(entity, uniqueCode + CollectionSuffix.ATTACHMENTS);
    }

    /**
     * 根据ID查询 附件信息
     *
     * @param id ID
     * @return 附件信息
     */
    public ComAttachmentsEntity getById(String uniqueCode, int id) {
        Criteria criteria = Criteria.where("id").is(id);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, ComAttachmentsEntity.class, uniqueCode + CollectionSuffix.ATTACHMENTS);
    }
}
