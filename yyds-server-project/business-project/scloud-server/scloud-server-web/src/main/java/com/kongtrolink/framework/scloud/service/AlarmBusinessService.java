package com.kongtrolink.framework.scloud.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.entity.Statistics;
import com.kongtrolink.framework.scloud.entity.FacadeView;
import com.kongtrolink.framework.scloud.query.AlarmBusinessQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/8 18:42
 * @Description:
 */
public interface AlarmBusinessService {

    void add(String uniqueCode, String table, AlarmBusiness business);

    boolean add(String uniqueCode, String table, List<AlarmBusiness> businessList);

    List<AlarmBusiness> listByKeyList(String uniqueCode, String table, List<String> keyList);

    boolean deleteByKeyList(String uniqueCode, String table, List<String> keyList);

    /**
     * @auther: liudd
     * @date: 2020/4/9 11:15
     * 功能描述:根据key消除告警
     */
    boolean resolveByKey(String unuqieCode, String table, AlarmBusiness alarmBusiness);

    List<AlarmBusiness> list(String uniqueCode, AlarmBusinessQuery alarmBusinessQuery);

    int count(String uniqueCode, AlarmBusinessQuery alarmBusinessQuery);

    /**
     * @auther: liudd
     * @date: 2020/4/9 16:26
     * 功能描述:告警确认
     */
    JSONObject check(String uniqueCode, String table, AlarmBusinessQuery businessQuery);

    JSONObject unCheck(String uniqueCode, String table, AlarmBusinessQuery businessQuery);

    /**
     * @auther: liudd
     * @date: 2020/4/9 17:31
     * 功能描述:告警消除
     */
    boolean resolve(String uniqueCode, String table, AlarmBusinessQuery businessQuery);

    boolean unResolveByKeys(String uniqueCode, String table, List<String> keyList);

    /**
     * @auther: liudd
     * @date: 2020/4/15 16:14
     * 功能描述:根据等级统计
     */
    List<Statistics> countLevel(String uniqueCode, String table, AlarmBusinessQuery businessQuery);

    /**
     * @auther: liudd
     * @date: 2020/4/14 15:59
     * 功能描述:告警频发站点统计
     */
    List<Statistics> alarmSiteTopHistory(String uniqueCode, AlarmBusinessQuery businessQuery);

    /**
     * @auther: liudd
     * @date: 2020/4/15 11:19
     * 功能描述:告警分布
     */
    List<Statistics> getAlarmDistributeList(String uniqueCode, AlarmBusinessQuery businessQuery);
}
