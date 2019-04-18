package com.kongtrolink.framework.execute.module.dao;

import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import com.kongtrolink.framework.core.entity.MongoTableName;
import com.kongtrolink.framework.execute.module.model.AlarmSignalConfigModel;
import com.kongtrolink.framework.execute.module.model.SignalModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by mystoxlol on 2019/3/31, 20:20.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class ConfigDao {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<AlarmSignalConfig> findAlarmSignalConfigByDevId(String devId) {

        return mongoTemplate.find(Query.query(Criteria.where("deviceId").is(devId)), AlarmSignalConfig.class, MongoTableName.ALARM_SIGNAL_CONFIG);
    }

    public List<AlarmSignalConfigModel> findAlarmSignalModelByDevType(Integer devType) {
        return mongoTemplate.find(Query.query(Criteria.where("devType").is(devType)), AlarmSignalConfigModel.class, MongoTableName.ALARM_SIGNAL_CONFIG_MODEL);
    }

    public void saveAlarmSignalConfig(List<AlarmSignalConfig> alarmSignals) {
        mongoTemplate.insert(alarmSignals, MongoTableName.ALARM_SIGNAL_CONFIG);
    }

    public void saveAlarmSignalConfig(AlarmSignalConfig alarmSignal) {
        mongoTemplate.save(alarmSignal, MongoTableName.ALARM_SIGNAL_CONFIG);
    }

    public AlarmSignalConfig findAlarmSignalConfigById(String id) {
        return mongoTemplate.findById(id, AlarmSignalConfig.class, MongoTableName.ALARM_SIGNAL_CONFIG);
    }

    public List<AlarmSignalConfig> findAlarmSignalConfigByDeviceIdAndCoId(String deviceId, String coId) {
        Criteria criteria = Criteria.where("deviceId").is(deviceId);
        if (StringUtils.isEmpty(coId)) {
            criteria.and("dataId").is(coId);
        }
        return mongoTemplate.find(Query.query(criteria),AlarmSignalConfig.class,MongoTableName.ALARM_SIGNAL_CONFIG);
    }

    public SignalModel findSignalModelByDeviceTypeAndCoId(int deviceType, String coId) {
       return mongoTemplate.findOne(Query.query(Criteria.where("deviceType").is(deviceType).and("dataId").is(coId)),SignalModel.class, MongoTableName.SIGNAL_MODEL);
    }
}
