package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.WorkConfig;
import com.kongtrolink.framework.scloud.query.WorkConfigQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 10:29
 * @Description:
 */
public interface WorkConfigService {

    void add(String uniqueCode, WorkConfig workConfig);

    boolean delete(String uniqueCode, String workConfigId);

    boolean update(String uniqueCode, WorkConfig workConfig);

    List<WorkConfig> list(String uniqueCode, WorkConfigQuery workConfigQuery);

    int count(String uniqueCode, WorkConfigQuery workConfigQuery);

    WorkConfig getById(String uniqueCode, String workConfigId);

    /**
     * @auther: liudd
     * @date: 2020/4/2 14:15
     * 功能描述:自动派单匹配告警工单
     */
    WorkConfig matchAutoConfig(String uniqueCode, Alarm alarm, String siteType, String sendWorkType);

    /**
     * @auther: liudd
     * @date: 2020/4/2 10:40
     * 功能描述:手动派单匹配工单配置
     */
    WorkConfig matchManualConfig(String uniqueCode, String siteCode);

    /**
     * @auther: liudd
     * @date: 2020/4/2 10:47
     * 功能描述:获取默认告警工单配置
     */
    WorkConfig getDefaultConfig(String uniqueCode);

    /**
     * @auther: liudd
     * @date: 2020/4/2 10:52
     * 功能描述:创建默认告警工单配置
     */
    WorkConfig createDefaultConfig(String uniqueCode);

}
