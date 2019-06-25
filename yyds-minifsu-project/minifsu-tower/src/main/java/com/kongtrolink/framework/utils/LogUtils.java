package com.kongtrolink.framework.utils;

import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 日志模块
 */
@Component
public class LogUtils {

    @Value("${server.bindIp}")
    private String host;
    @Value("${server.name}")
    private String name;

    @Autowired
    LogDao logDao;

    /**
     * 保存日志
     * @param msgId
     * @param sn
     * @param msgType
     * @param stateCode
     */
    public void saveLog(String msgId, String sn, String msgType, int stateCode) {
        Log log = new Log(new Date(System.currentTimeMillis()),
                stateCode,
                sn, msgType, msgId, name, host);
        logDao.save(log);
    }
}
