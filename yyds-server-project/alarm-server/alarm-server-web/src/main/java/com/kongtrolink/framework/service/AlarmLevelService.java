package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.AlarmLevel;
import com.kongtrolink.framework.enttiy.DeviceTypeLevel;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.query.AlarmLevelQuery;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:06
 * @Description:
 */
public interface AlarmLevelService {

    boolean save(AlarmLevel alarmLevel);

    boolean save(List<AlarmLevel> alarmLevelList);

    boolean delete(String alarmLevelId);

    boolean update(AlarmLevel alarmLevel);

    List<AlarmLevel> list(AlarmLevelQuery levelQuery);

    int count(AlarmLevelQuery levelQuery);

    AlarmLevel getOne(AlarmLevelQuery alarmLevelQuery);

    boolean isRepeat(AlarmLevel alarmLevel);

    /**
     * @auther: liudd
     * @date: 2019/9/21 9:59
     * 功能描述：根据uniqueCode， service，deviceType， deviceModel, sourceLevel获取告警等级
     * 如果没有，则使用该系统默认告警点等级
     * 并将新告警等级和告警颜色等存入告警
     */
    AlarmLevel getAlarmLevel(AlarmLevelQuery levelQuery);

    AlarmLevel createAlarmLevel(EnterpriseLevel enterpriseLevel, DeviceTypeLevel deviceTypeLevel);

    /**
     * @auther: liudd
     * @date: 2019/10/16 15:41
     * 功能描述:根据设备类型信息删除告警等级
     */
    int deleteList(String enterpriseCode, String serverCode, String deviceType, String deviceModel);


    List<AlarmLevel> getByEntDevCodeList(List<String> entDevCodeList);
}
