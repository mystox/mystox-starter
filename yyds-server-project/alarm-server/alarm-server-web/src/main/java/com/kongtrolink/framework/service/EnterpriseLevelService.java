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

    boolean updateState(EnterpriseLevelQuery enterpriseLevelQuery);

    /**
     * @auther: liudd
     * @date: 2019/10/16 15:02
     * 功能描述:获取最后一次启用的企业告警
     */
    List<EnterpriseLevel> getLastUse(String enterpriseCode, String serverCode);

    void addAlarmLevelByEnterpriseInfo(String enterpriseCode, String serverCode);

    /**
     * @auther: liudd
     * @date: 2019/10/16 20:18
     * 功能描述:禁用原来的告警等级
     */
    boolean forbitBefor(String enterpriseCode, String serverCode, Date updateTime);

    /**
     * @auther: liudd
     * @date: 2019/10/28 14:27
     * 功能描述:初始化默认系统告警等级
     */
    void initEnterpriseLevel();

    /**
     * @auther: liudd
     * @date: 2019/11/6 16:23
     * 功能描述:调用远程接口修改告警等级模块中企业等级
     */
    void updateEnterpriseLevelMap(String enterpriseCode, String serverCode, String enterpriseLevelCode, String type);

    /**
     * @auther: liudd
     * @date: 2019/12/27 11:09
     * 功能描述:处理企业默认告警等级
     */
    void handleUniqueDefault(String enterpriseServer, String serverCode);

    /**
     * @auther: liudd
     * @date: 2019/12/27 17:46
     * 功能描述:根据规则名称获取
     */
    List<EnterpriseLevel> getByName(String enterpriseCode, String serverCode, String name);
}
