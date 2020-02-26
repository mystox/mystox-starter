package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.dao.CompanyMongo;
import com.kongtrolink.framework.scloud.entity.CompanyEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicCompanyEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicUserEntity;
import com.kongtrolink.framework.scloud.entity.model.CompanyModel;
import com.kongtrolink.framework.scloud.service.CompanyService;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统管理-企业管理 接口实现类
 * Created by Eric on 2020/2/5.
 */
@Service
public class CompanyServiceImpl implements CompanyService{

    @Autowired
    CompanyMongo companyMongo;
    @Autowired
    MqttOpera mqttOpera;

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyServiceImpl.class);

    /**
     * 获取企业信息前端显示模型
     *
     * @param companyEntity
     */
    @Override
    public CompanyModel getCompanyModel(CompanyEntity companyEntity) {
        CompanyModel companyModel = new CompanyModel();
        companyModel.setUniqueCode(companyEntity.getUniqueCode());

        //从【云管】获取企业基本信息
        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_ENTERPRISE_MSG, companyEntity.getUniqueCode());
        int stateCode = msgResult.getStateCode();
        if (1 == stateCode){
            BasicCompanyEntity basicCompanyEntity = JSONObject.parseObject(msgResult.getMsg(), BasicCompanyEntity.class);
            companyModel.setCompanyName(basicCompanyEntity.getName());

            LOGGER.info("【企业管理】,从【云管】获取企业信息成功");
        }else {
            LOGGER.error("【企业管理】,从【云管】获取企业信息失败");
        }

        companyModel.setContactsId(companyEntity.getContactsId());
        //从【云管】获取企业下所有用户
        MsgResult msgResult1 = mqttOpera.opera(OperaCodeConstant.GET_USER_LIST_BY_ENTERPRISE_CODE, companyEntity.getUniqueCode());
        int stateCode1 = msgResult1.getStateCode();
        if (1 == stateCode1){
            List<BasicUserEntity> basicUserEntityList = JSONArray.parseArray(msgResult1.getMsg(), BasicUserEntity.class);
            if (basicUserEntityList.size() > 0){
                for (BasicUserEntity basicUserEntity : basicUserEntityList){
                    if (basicUserEntity.getId().equals(companyEntity.getContactsId())){
                        companyModel.setContactsName(basicUserEntity.getName());
                        companyModel.setContactsPhone(basicUserEntity.getPhone());
                    }
                }
            }

            LOGGER.info("【企业管理】,从【云管】获取所有用户成功");
        }else {
            LOGGER.error("【企业管理】,从【云管】获取所有用户失败");
        }

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
