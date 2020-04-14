package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.RedefineAlarm;
import com.kongtrolink.framework.scloud.query.RedefineAlarmQuery;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/13 14:54
 * @Description:
 */
public interface RedefineAlarmService {

    boolean add(String uniqueCode, RedefineAlarm redefineAlarm);

    List<RedefineAlarm> list(String uniqueCode, RedefineAlarmQuery redefineAlarmQuery);

    int count(String uniqueCode, RedefineAlarmQuery redefineAlarmQuery);
}
