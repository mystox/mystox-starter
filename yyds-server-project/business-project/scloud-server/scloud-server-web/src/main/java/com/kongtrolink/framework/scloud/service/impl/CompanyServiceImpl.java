package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.CompanyMongo;
import com.kongtrolink.framework.scloud.entity.CompanyEntity;
import com.kongtrolink.framework.scloud.entity.model.CompanyModel;
import com.kongtrolink.framework.scloud.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统管理-企业管理 接口实现类
 * Created by Eric on 2020/2/5.
 */
@Service
public class CompanyServiceImpl implements CompanyService{

    @Autowired
    CompanyMongo companyMongo;

    /**
     * 获取企业信息前端显示模型
     *
     * @param companyEntity
     */
    @Override
    public CompanyModel getCompanyModel(CompanyEntity companyEntity) {
        CompanyModel companyModel = new CompanyModel();
        companyModel.setUniqueCode(companyEntity.getUniqueCode());
        // TODO: 2020/2/11 待云管开发好对外接口
//        companyModel.setCompanyName();
//        companyModel.setServerName();
//        companyModel.setAgent();
        companyModel.setContactsId(companyEntity.getContactsId());
        // TODO: 2020/2/11 待云管开发好对外接口
//        companyModel.setContactsName();
//        companyModel.setContactsPhone();
        companyModel.setPollingInterval(companyEntity.getPollingInterval());
        companyModel.setRefreshInterval(companyEntity.getRefreshInterval());
        companyModel.setFsuOfflineDelayTime(companyEntity.getFsuOfflineDelayTime());
        companyModel.setAlarmReminderOpen(companyEntity.isAlarmReminderOpen());
        companyModel.setAlarmLevels(companyEntity.getAlarmLevels());
        companyModel.setAlarmFields(companyEntity.getAlarmFields());
        companyModel.setCycleTimes(companyEntity.getCycleTimes());
        companyModel.setFlashFrequency(companyEntity.getFlashFrequency());

        return companyModel;
    }

    /**
     * 修改企业扩展信息及配置信息
     */
    @Override
    public void updateCompany(String uniqueCode, CompanyEntity companyEntity) {
        companyMongo.updateCompany(uniqueCode, companyEntity);
    }
}
