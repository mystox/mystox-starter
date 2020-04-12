package com.kongtrolink.framework.scloud.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.entity.Alarm;
import com.kongtrolink.framework.scloud.entity.AlarmBusiness;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.mongodb.DBObject;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 14:54
 * @Description:
 */
public interface AlarmService {

    /**
     * @auther: liudd
     * @date: 2020/3/3 14:01
     * 功能描述:获取列表
     */
    JsonResult list(AlarmQuery alarmQuery) throws Exception;

    /**
     * @auther: liudd
     * @date: 2020/3/3 15:59
     * 功能描述:填充设备，站点等信息
     */
    void initInfo(String uniqueCode, String serverCode, List<AlarmBusiness> businessList);

    JSONObject operate(AlarmQuery alarmQuery) throws Exception;

    JSONObject updateWorkInfo(AlarmQuery alarmQuery);
}
