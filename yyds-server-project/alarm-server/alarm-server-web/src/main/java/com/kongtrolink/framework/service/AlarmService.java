package com.kongtrolink.framework.service;

import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.query.AlarmQuery;
import com.mongodb.DBObject;

import java.util.Date;
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

    /**
     * @auther: liudd
     * @date: 2019/12/5 19:20
     * 功能描述:历史告警伪分页
     */
    ListResult<DBObject> historyAlarmList(AlarmQuery alarmQuery);

    /**
     * @auther: liudd
     * @date: 2019/12/28 15:06
     * 功能描述:确认告警
     */
    boolean check(String key, String table, Date date, String checkContant, FacadeView checker);
}
