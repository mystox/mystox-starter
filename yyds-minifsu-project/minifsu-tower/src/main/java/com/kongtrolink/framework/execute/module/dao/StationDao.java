package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.entity.MongoDBTable;
import com.kongtrolink.framework.jsonType.JsonStation;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * @author fengw
 * FSU绑定数据库操作
 * 新建文件 2019-4-16 11:10:26
 */
@Component
public class StationDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 通过fsuId查找绑定信息
     * @param fsuId fsuId
     * @return 绑定信息，不为null则说明存在已绑定信息
     */
    public JsonStation getInfoByFsuId(String fsuId) {
        Criteria criteria = Criteria
                .where("disabled").is(false)
                .and("fsuId").is(fsuId);
        return mongoTemplate.findOne(Query.query(criteria),
                JsonStation.class, MongoDBTable.T_STATION);
    }

    /**
     * 通过sn查找绑定信息
     * @param sn sn
     * @return 绑定信息，不为null则说明存在已绑定信息
     */
    public JsonStation getInfoBySn(String sn) {
        Criteria criteria = Criteria
                .where("disabled").is(false)
                .and("sn").is(sn);
        return mongoTemplate.findOne(Query.query(criteria),
                JsonStation.class, MongoDBTable.T_STATION);
    }

    /**
     * 插入绑定记录
     * @param info 待插入的绑定记录
     */
    public void insertInfo(JsonStation info) {
        mongoTemplate.insert(info, MongoDBTable.T_STATION);
    }

    /**
     * 更新绑定信息
     * @param info 待更新的绑定信息
     * @return 是否绑定成功
     */
    public boolean updateInfoByFsuIdAndSn(JsonStation info) {
        boolean result;

        Criteria criteria = Criteria
                .where("fsuId").is(info.getFsuId())
                .and("sn").is(info.getSn())
                .and("disabled").is(false);

        Update update = new Update();
        update.set("name", info.getName());
        update.set("address", info.getAddress());
        update.set("desc", info.getDesc());
        update.set("dictMode", info.getDictMode());
        update.set("vpnName", info.getVpnName());

        WriteResult writeResult = mongoTemplate.updateMulti(Query.query(criteria),
                update, MongoDBTable.T_STATION);

        result = writeResult.getN() > 0;

        return result;
    }

    /**
     * 将指定fsuId和sn的记录设置为解绑状态
     * @param sn 待解绑的sn
     * @param fsuId 带解绑的fsuId
     * @return 是否解绑成功
     */
    public boolean unbindByFsuIdAndSn(String sn, String fsuId) {
        boolean result = false;

        Criteria criteria = Criteria
                .where("fsuId").is(fsuId)
                .and("sn").is(sn)
                .and("disabled").is(false);

        Update update = new Update();
        update.set("disabled", true);
        update.set("disabledTime", System.currentTimeMillis() / 1000);

        WriteResult writeResult = mongoTemplate.updateMulti(Query.query(criteria),
                update, MongoDBTable.T_STATION);

        result = writeResult.getN() > 0;

        return result;
    }
}
