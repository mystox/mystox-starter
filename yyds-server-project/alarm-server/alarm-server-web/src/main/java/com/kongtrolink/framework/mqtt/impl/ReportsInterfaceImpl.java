package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.dao.AlarmReduceDao;
import com.kongtrolink.framework.enttiy.AlarmCycle;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import com.kongtrolink.framework.mqtt.ReportsInterface;
import com.kongtrolink.framework.query.AlarmQuery;
import com.kongtrolink.framework.query.EnterpriseLevelQuery;
import com.kongtrolink.framework.service.AlarmCycleService;
import com.kongtrolink.framework.service.EnterpriseLevelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/10 16:01
 * \* Description:
 * \
 */
@Service
public class ReportsInterfaceImpl implements ReportsInterface {

    Logger logger = LoggerFactory.getLogger(ReportsInterfaceImpl.class);

    @Autowired
    AlarmReduceDao alarmReduceDao;
    @Autowired
    AlarmDao alarmDao;

    @Autowired
    EnterpriseLevelService enterpriseLevelService;

    @Autowired
    AlarmCycleService alarmCycleService;

    @Override
    public List<JSONObject> getAlarmCountByDeviceIdList(String msg) {
        logger.info("alarm count report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        List<JSONObject> result = alarmReduceDao.AlarmCount(alarmQuery);
        return result;
    }

    @Override
    public List<JSONObject> getAlarmsByDeviceIdList(String msg) {
        logger.info("alarm details report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        return alarmReduceDao.getAlarmHistory(alarmQuery);

    }

    @Override
    public List<JSONObject> getCurrentAlarmsByDeviceIdList(String msg) {
        logger.info("alarm details report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        return alarmReduceDao.getAlarmCurrent(alarmQuery);

    }

    @Override
    public List<JSONObject> getAlarmCategoryByDeviceIdList(String msg) {
        logger.info("alarm Category report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        return alarmReduceDao.getAlarmCategory(alarmQuery);
    }

    @Override
    public List<JSONObject> getAlarmCurrentCategoryByDeviceIdList(String msg) {
        logger.info("alarm Category report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        return alarmReduceDao.getAlarmCurrentCategory(alarmQuery);
    }

    @Override
    public JSONObject getFsuOfflineStatistic(String msg) {
        logger.info("fsu offline statistic report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        return alarmReduceDao.fsuOfflineStatistic(alarmQuery);
    }

    @Override
    public List<JSONObject> getFsuOfflineDetails(String msg) {
        logger.info("fsu off statistic report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        alarmQuery.setName("FSU离线告警");
        return alarmReduceDao.getAlarmHistory(alarmQuery);
    }

    @Override
    public JSONObject stationOffStatistic(String msg) {
        logger.info("station off statistic report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        return alarmReduceDao.stationOffStatistic(alarmQuery);
    }

    @Override
    public List<JSONObject> getStationOffDetails(String msg) {
        logger.info("station off details report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        alarmQuery.setName("交流输入XX停电告警");
        return alarmReduceDao.getAlarmHistory(alarmQuery);
    }

    @Override
    public JSONObject getStationBreakStatistic(String msg) {
        logger.info("station beak statistic report receive...[{}]", msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg, AlarmQuery.class);
        alarmQuery.setName("一级低压脱离告警");
        return alarmReduceDao.stationBreakStatistic(alarmQuery);
    }

    @Override
    public List<EnterpriseLevel> getAlarmLevel(String body) {
        EnterpriseLevelQuery enterpriseLevelQuery = JSONObject.parseObject(body, EnterpriseLevelQuery.class);
        List<EnterpriseLevel> lastUse = enterpriseLevelService.getLastUse(enterpriseLevelQuery.getEnterpriseCode(), enterpriseLevelQuery.getServerCode());
        return lastUse;
    }

    @Override
    public AlarmCycle getAlarmCycle(String body) {
        JSONObject query = JSONObject.parseObject(body);
        AlarmCycle lastUse = alarmCycleService.getLastUse(query.getString("enterpriseCode"), query.getString("serverCode"));
        return lastUse;
    }


}