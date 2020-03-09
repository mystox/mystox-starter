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
     * @date: 2020/2/28 13:54
     * 功能描述:获取列表
     */
    List<DBObject> list(AlarmQuery alarmQuery);

    /**
     * @auther: liudd
     * @date: 2019/12/28 15:06
     * 功能描述:确认告警
     */
    boolean check(String key, String table, Date date, String checkContant, FacadeView checker);

    /**
     * @auther: liudd
     * @date: 2020/3/2 13:28
     * 功能描述:告警确认，后期将中台的告警确认方法合并过来
     */
    boolean check(AlarmQuery alarmQuery);

    /**
     * @auther: liudd
     * @date: 2020/3/2 16:57
     * 功能描述:告警消除
     */
    boolean resolve(AlarmQuery alarmQuery);
}
