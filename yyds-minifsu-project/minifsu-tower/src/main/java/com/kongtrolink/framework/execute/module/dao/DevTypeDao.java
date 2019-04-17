package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.config.MongoConfig;
import com.kongtrolink.framework.execute.module.model.DevType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengw
 * 设备类型数据库操作
 * 新建文件 2019-4-17 09:28:21
 */
@Component
public class DevTypeDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 查询指定内部设备类型对应的信息
     * @param type 指定内部设备类型
     * @return 对照信息
     */
    public DevType getInfoByType(String type) {
        Criteria criteria = Criteria.where("type").is(type);
        return mongoTemplate.findOne(Query.query(criteria),
                DevType.class, MongoConfig.T_DEVICE_LIST);
    }

    /**
     * 查询所有对照信息
     * @return 返回Map，铁塔设备类型为key，内部设备类型为value
     */
    public Map<String, String> getAll() {
        Map<String, String> result = new HashMap();

        List<DevType> list = mongoTemplate.findAll(DevType.class,
                MongoConfig.T_DEV_TYPE_MAP);

        for (int i = 0; i < list.size(); ++i) {
            result.put(list.get(i).getCntbType(), list.get(i).getType());
        }

        return result;
    }
}
