package com.kongtrolink.framework.scloud.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.query.WorkQuery;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 13:27
 * @Description:
 */
public interface WorkService {

    void add(String uniqueCode, Work work);

    boolean delete(String uniqueCode, String workId);

    boolean update(String uniqueCode, Work work);

    List<Work> list(String uniqueCode, WorkQuery workQuery);

    int count(String uniqueCode, WorkQuery workQuery);

    Work getNotOverByDeviceCode(String uniqueCode, String deviceCode);

    Work createWork(WorkQuery workQuery, WorkConfig workConfig, String sendType);

    Work getById(String uniqueCode, String workId);

    /**
     * @auther: liudd
     * @date: 2020/4/2 12:23
     * 功能描述:接单公共部分
     */
    JsonResult receCommon(String uniqueCode, String workId, User user, Date curDate, String FTU);

    /**
     * @auther: liudd
     * @date: 2020/4/2 13:19
     * 功能描述:转派公共部分
     */
    JsonResult redeployCommon(String uniqueCode, WorkRecord workRecord, User user, Date curDate, String FTU);

    JsonResult urgeCommon(String uniqueCode, WorkRecord workRecord, User user, Date curDate, String FTU);

    JsonResult overCommon(String uniqueCode, WorkRecord workRecord, User user, Date curDate, String FTU);

    JsonResult cancelCommon(String uniqueCode, WorkRecord workRecord, User user, Date curDate, String FTU);

    JsonResult detailCommon(String uniqueCode, String workId);

    /**
     * @auther: liudd
     * @date: 2020/4/2 17:18
     * 功能描述:告警消除，联动修改工单
     */
    void resolveAlarm(String uniqueCode, List<Alarm> alarmList);

    JSONObject createJpush(Work work, String operate, List<String> accountList);

}
