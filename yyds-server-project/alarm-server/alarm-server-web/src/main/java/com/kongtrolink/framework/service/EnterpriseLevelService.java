package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:44
 * @Description:
 */
public interface EnterpriseLevelService {

    void add(EnterpriseLevel enterpriseLevel);

    boolean delete(String enterpriseLevelId);

    boolean deleteByCode(String code);

    boolean update(EnterpriseLevel enterpriseLevel);

    EnterpriseLevel get(String enterpriseLevelId);

    List<EnterpriseLevel> list(EnterpriseLevelQuery levelQuery);

    int count(EnterpriseLevelQuery levelQuery);

    EnterpriseLevel getOne(EnterpriseLevelQuery levelQuery);


    boolean updateState(EnterpriseLevelQuery enterpriseLevelQuery);

    /**
     * @auther: liudd
     * @date: 2019/10/16 15:02
     * 功能描述:获取最后一次启用的企业告警
     */
    List<EnterpriseLevel> getLastUse(String enterpriseCode, String serverCode);

    List<EnterpriseLevel> getDefault();

    void addAlarmLevelByEnterpriseInfo(String enterpriseCode, String serverCode);

    /**
     * @auther: liudd
     * @date: 2019/10/16 20:18
     * 功能描述:禁用原来的告警等级
     */
    boolean forbitBefor(String enterpriseCode, String serverCode, Date updateTime);
}
