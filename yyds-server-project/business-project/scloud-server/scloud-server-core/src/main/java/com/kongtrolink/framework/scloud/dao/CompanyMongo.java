package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.constant.CollectionSuffix;
import com.kongtrolink.framework.scloud.entity.CompanyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 企业相关数据查询类
 * Created by Eric on 2020/2/10.
 */
@Repository
public class CompanyMongo {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 根据企业唯一码获取企业扩展信息及配置信息
     */
    public CompanyEntity findCompanyInfo(String uniqueCode){
        return mongoTemplate.findOne(new Query(Criteria.where("uniqueCode").is(uniqueCode)), CompanyEntity.class, CollectionSuffix.COMPANY_INFO);
    }

    /**
     * 修改企业扩展信息及配置信息
     */
    public void updateCompany(String uniqueCode, CompanyEntity companyEntity){
        String contactsId = companyEntity.getContactsId();
        int pollingInterval = companyEntity.getPollingInterval();
        int refreshInterval = companyEntity.getRefreshInterval();
        int fsuOfflineDelayTime = companyEntity.getFsuOfflineDelayTime();
        boolean alarmReminderOpen = companyEntity.isAlarmReminderOpen();
        List<String> alarmLevels = companyEntity.getAlarmLevels();
        List<String> alarmFields = companyEntity.getAlarmFields();
        int cycleTimes = companyEntity.getCycleTimes();
        int flashFrequency = companyEntity.getFlashFrequency();

        Update update = new Update();
        if (contactsId != null) {
            update.set("contactsId", contactsId);
        }
        if (alarmLevels != null){
            update.set("alarmLevels", alarmLevels);
        }
        if (alarmFields != null){
            update.set("alarmFields", alarmFields);
        }
        update.set("pollingInterval", pollingInterval);
        update.set("refreshInterval", refreshInterval);
        update.set("fsuOfflineDelayTime", fsuOfflineDelayTime);
        update.set("alarmReminderOpen", alarmReminderOpen);
        update.set("cycleTimes", cycleTimes);
        update.set("flashFrequency", flashFrequency);

        Query query = Query.query(Criteria.where("uniqueCode").is(uniqueCode));
        mongoTemplate.updateFirst(query, update, CollectionSuffix.COMPANY_INFO);
    }
}
