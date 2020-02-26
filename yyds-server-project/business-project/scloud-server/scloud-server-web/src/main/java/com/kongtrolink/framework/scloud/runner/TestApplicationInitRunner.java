package com.kongtrolink.framework.scloud.runner;

import com.kongtrolink.framework.scloud.dao.CompanyMongo;
import com.kongtrolink.framework.scloud.entity.CompanyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 【测试】初始化数据
 * Created by Eric on 2020/2/19.
 */
@Component
public class TestApplicationInitRunner  implements ApplicationRunner{
    @Autowired
    CompanyMongo companyMongo;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        String uniqueCode = "YYDS";
        CompanyEntity entity = companyMongo.findCompanyInfo(uniqueCode);
        if (entity == null) {

            CompanyEntity companyEntity = new CompanyEntity();
            companyEntity.setUniqueCode(uniqueCode);
            companyEntity.setContactsId("97b933e7-65dd-4e2d-bdcf-e2bc71818b60");
            List<String> alarmLevels = new ArrayList<>();
            alarmLevels.add("一级");
            alarmLevels.add("二级");
            alarmLevels.add("三级");
            alarmLevels.add("四级");

            List<String> alarmFields = new ArrayList<>();
            alarmFields.add("区域");
            alarmFields.add("站点");
            alarmFields.add("机房");
            alarmFields.add("设备");
            alarmFields.add("信号");
            alarmFields.add("告警编码ID");
            alarmFields.add("告警级别");
            alarmFields.add("告警时间");

            companyEntity.setAlarmLevels(alarmLevels);
            companyEntity.setAlarmFields(alarmFields);

            companyMongo.addCompany(companyEntity);
        }
    }
}
