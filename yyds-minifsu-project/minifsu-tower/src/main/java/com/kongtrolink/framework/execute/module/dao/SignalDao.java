package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.entity.MongoDBTable;
import com.kongtrolink.framework.execute.module.model.Signal;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fengw
 * 信号点数据库操作
 * 新建文件 2019-4-19 08:33:49
 */
@Component
public class SignalDao {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 获取指定内部类型下的指定信号点信息
     * @param type 内部设备类型
     * @param signalId 内部信号点Id
     * @return 信号点信息
     */
    public Signal getInfoByTypeAndSignalId(int type, String signalId) {
        Signal result;

        Criteria criteria = Criteria
                .where("type").is(type)
                .and("signalId").is(signalId);

        result = mongoTemplate.findOne(Query.query(criteria),
                Signal.class, MongoDBTable.T_SIGNAL);

        return result;
    }

    /**
     * 获取指定内部类型下的指定铁塔信号点信息
     * @param type 内部设备类型
     * @param cntbId 铁塔信号点Id
     * @return 信号点信息
     */
    public Signal getInfoByTypeAndCntbId(int type, String cntbId) {
        Signal result;

        Criteria criteria = Criteria
                .where("type").is(type)
                .and("cntbId").is(cntbId);

        result = mongoTemplate.findOne(Query.query(criteria),
                Signal.class, MongoDBTable.T_SIGNAL);

        return result;
    }

    /**
     * 获取指定内部设备下的所有信号点信息
     * @param type 内部设备类型
     * @return 信号点列表
     */
    public List<Signal> getListByType(int type) {
        List<Signal> result;

        Criteria criteria = Criteria
                .where("type").is(type);

        result = mongoTemplate.find(Query.query(criteria),
                Signal.class, MongoDBTable.T_SIGNAL);

        return result;
    }

    /**
     * 更新数据库中的信息
     * @param signal 待更新信息
     * @return 更新结果
     */
    public boolean upsert(Signal signal) {
        boolean result;

        Criteria criteria = Criteria
                .where("type").is(signal.getType())
                .and("signalId").is(signal.getSignalId());

        Update update = new Update();
        update.set("cntbId", signal.getCntbId());
        update.set("name", signal.getName());
        update.set("idType", signal.getIdType());
        update.set("simpleEnable", signal.isSimpleEnable());
        update.set("unit", signal.getUnit());

        WriteResult writeResult = mongoTemplate.upsert(Query.query(criteria),
                update, MongoDBTable.T_SIGNAL);

        result = writeResult.getN() > 0;

        return result;
    }
}
