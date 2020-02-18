package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.CompanyEntity;
import com.kongtrolink.framework.scloud.entity.model.CompanyModel;

/**
 * 系统管理-企业管理 接口类
 * Created by Eric on 2020/2/5.
 */
public interface CompanyService {

    /**
     * 获取企业信息前端显示模型
     */
    CompanyModel getCompanyModel(CompanyEntity companyEntity);

    /**
     * 修改企业扩展信息及配置信息
     */
    void updateCompany(String uniqueCode, CompanyEntity companyEntity);
}
