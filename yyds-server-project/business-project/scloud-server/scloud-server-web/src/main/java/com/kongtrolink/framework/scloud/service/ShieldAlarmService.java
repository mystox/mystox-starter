package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.ShieldAlarm;
import com.kongtrolink.framework.scloud.query.ShieldAlarmQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/3/5 09:18
 * @Description: 被屏蔽告警
 */
public interface ShieldAlarmService {

    boolean add(String uniqueCode, ShieldAlarm shieldAlarm);

    List<ShieldAlarm> list(String uniqueCode, ShieldAlarmQuery shieldAlarmQuery);

    int count(String uniqueCode, ShieldAlarmQuery shieldAlarmQuery);

    void initInfo(String uniqueCode, List<ShieldAlarm> shieldAlarmList);
}
