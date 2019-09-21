package com.kongtrolink.service;

import com.kongtrolink.enttiy.EnterpriseLevel;
import com.kongtrolink.query.EnterpriseLevelQuery;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/20 10:44
 * @Description:
 */
public interface EnterpriseLevelService {

    void add(EnterpriseLevel enterpriseLevel);

    boolean delete(String enterpriseLevelId);

    boolean update(EnterpriseLevel enterpriseLevel);

    List<EnterpriseLevel> list(EnterpriseLevelQuery levelQuery);

    int count(EnterpriseLevelQuery levelQuery);

    EnterpriseLevel getOne(EnterpriseLevelQuery levelQuery);

    /**
     * @auther: liudd
     * @date: 2019/9/20 11:17
     * 功能描述:判定原告警是否重复
     */
    boolean isRepeat(EnterpriseLevel enterpriseLevel);

    /**
     * @auther: liudd
     * @date: 2019/9/21 10:04
     * 功能描述:设置默认告警等级，一个企业只有一个默认告警等级
     */
    boolean updateDefault(EnterpriseLevelQuery enterpriseLevelQuery);
}
