package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.entity.MongoDBTable;
import com.kongtrolink.framework.execute.module.model.Vpn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author fengw
 * Vpn信息数据库操作
 * 新建文件 2019-4-17 13:59:42
 */
@Component
public class VpnDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 获取指定名称的Vpn信息
     * @param vpnName Vpn名称
     * @return Vpn信息
     */
    public Vpn getInfoByName(String vpnName) {
        Criteria criteria = Criteria
                .where("vpnName").is(vpnName);
        return mongoTemplate.findOne(Query.query(criteria),
                Vpn.class, MongoDBTable.T_VPN);
    }
}
