package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.ImageFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * Created by Eric on 2020/2/25.
 */
@Repository
public class ImageFileMongo {
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 保存 上传的 图片信息
     *
     * @param info 图片信息-二进制保存
     */
    public void saveImageFile(ImageFileInfo info) {
        mongoTemplate.save(info);
    }

    /**
     * 根据 主键ID 查询 图片信息
     */
    public ImageFileInfo getInfo(String imgType, Integer id) {
        Criteria criteria = Criteria.where("id").is(id)
                .and("type").is(imgType);
        return mongoTemplate.findOne(new Query(criteria), ImageFileInfo.class);
    }
}
