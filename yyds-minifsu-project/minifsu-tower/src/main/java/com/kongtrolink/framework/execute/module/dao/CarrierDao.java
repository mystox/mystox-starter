package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.execute.module.model.Carrier;
import com.kongtrolink.framework.entity.MongoDBTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengw
 * 运营商数据库操作
 * 新建文件 2019-4-17 18:33:19
 */
@Component
public class CarrierDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 查询所有对照信息
     * @return 返回Map，内部服务上报类型为key，上报铁塔类型为value
     */
    public Map<String, String> getAll() {
        Map<String, String> result = new HashMap();

        List<Carrier> list = mongoTemplate.findAll(Carrier.class,
                MongoDBTable.T_CARRIER);

        for (int i = 0; i < list.size(); ++i) {
            result.put(list.get(i).getType(), list.get(i).getCntbType());
        }

        return result;
    }
}
