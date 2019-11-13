package com.kongtrolink.framework.service;

import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.query.AlarmQuery;
import com.mongodb.DBObject;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:42
 * @Description:
 */
public interface AlarmService {
    /**
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:列表
     */
    List<DBObject> list(AlarmQuery alarmQuery, String table);

    /**
     * @auther: liudd
     * @date: 2019/9/11 14:48
     * 功能描述:统计
     */
    int count(AlarmQuery alarmQuery, String table);

    /**
     * @auther: liudd
     * @date: 2019/11/13 16:59
     * 功能描述:前端获取历史告警表
     */
    ListResult<DBObject> getHistoryAlarmList(AlarmQuery alarmQuery);
}
