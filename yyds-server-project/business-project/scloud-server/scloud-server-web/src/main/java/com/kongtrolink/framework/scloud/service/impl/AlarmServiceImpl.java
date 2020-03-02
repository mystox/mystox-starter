package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.AlarmDao;
import com.kongtrolink.framework.scloud.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: liudd
 * @Date: 2020/2/26 14:55
 * @Description:
 */
@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    AlarmDao alarmDao;


}
