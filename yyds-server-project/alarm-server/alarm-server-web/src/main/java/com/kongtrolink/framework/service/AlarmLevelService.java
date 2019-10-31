package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:06
 * @Description:
 */
public interface AlarmLevelService {

    boolean save(AlarmLevel alarmLevel);

    boolean update(AlarmLevel alarmLevel);

    List<AlarmLevel> list(AlarmLevelQuery levelQuery);

    int count(AlarmLevelQuery levelQuery);

    /**
     * @auther: liudd
     * @date: 2019/10/16 15:41
     * 功能描述:根据设备类型信息删除告警等级
     */
    int deleteList(String enterpriseCode, String serverCode, String deviceType, String deviceModel);


    List<AlarmLevel> getByEntDevCodeList(List<String> entDevCodeList);
}
