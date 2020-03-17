package com.kongtrolink.framework.mqtt.impl;

import com.alibaba.fastjson.JSONObject;
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


    @Override
    public List<JSONObject> getAlarmCountByDeviceIdList(String msg) {
        logger.info("alarm count report receive...[{}]",msg);
        AlarmQuery alarmQuery = JSONObject.parseObject(msg,AlarmQuery.class);
        List<JSONObject> result = alarmReduceDao.AlarmCount(alarmQuery);


        return result;
    }




}