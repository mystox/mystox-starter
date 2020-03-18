package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.AlarmDao;
import com.kongtrolink.framework.dao.AlarmReduceDao;
import com.kongtrolink.framework.mqtt.ReportsInterface;
import com.kongtrolink.framework.query.AlarmQuery;
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


    @Override
    public List<JSONObject> getAlarmCountByDeviceIdList(String msg) {
        logger.info("alarm count report receive...[{}]",msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg,AlarmQuery.class);
        List<JSONObject> result = alarmReduceDao.AlarmCount(alarmQuery);
        return result;
    }

    @Override
    public List<JSONObject> getAlarmsByDeviceList(String msg) {
        logger.info("alarm details report receive...[{}]",msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg,AlarmQuery.class);
        return alarmReduceDao.getAlarmHistory(alarmQuery);

    }

    @Override
    public List<JSONObject> getAlarmCategoryByDeviceIdList(String msg) {
        logger.info("alarm Category report receive...[{}]",msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg,AlarmQuery.class);
        return alarmReduceDao.getAlarmCategory(alarmQuery);
    }


}