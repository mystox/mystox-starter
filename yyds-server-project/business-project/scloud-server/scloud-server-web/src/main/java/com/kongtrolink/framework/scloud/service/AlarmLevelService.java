package com.kongtrolink.framework.scloud.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.query.AlarmLevelQuery;

/**
 * @Auther: liudd
 * @Date: 2020/4/3 08:31
 * @Description:
 */
public interface AlarmLevelService {

    JSONObject getLastUse(AlarmLevelQuery alarmLevelQuery);

    JSONObject getDeviceTypeList(AlarmLevelQuery alarmLevelQuery);
}
