package com.kongtrolink.framework.service;

import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.query.AlarmQuery;
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
    List<Alarm> list(AlarmQuery alarmQuery);

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
     * @date: 2020/4/3 9:58
     * 功能描述:取消确认
     */
    boolean nocheck(AlarmQuery alarmQuery);

    /**
     * @auther: liudd
     * @date: 2020/3/2 16:57
     * 功能描述:告警消除
     */
    boolean resolve(AlarmQuery alarmQuery);
}
