package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import com.kongtrolink.framework.execute.module.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;

/**
 * Created by mystoxlol on 2019/3/29, 16:31.
 * company: kongtrolink
 * description:
 * update record:
 */
public class LogServiceImpl implements LogService {
    @Autowired
    LogDao logDao;


    @Override
    public JSONObject saveCleanupLog(ModuleMsg moduleMsg) {

        JSONObject msgPayload = moduleMsg.getPayload();

        //日志记录
        Log log = new Log();
        log.setMsgType(moduleMsg.getPktType());
        log.setErrorCode(1);
        log.setMsgId(moduleMsg.getMsgId());
        log.setHostName((String) msgPayload.get("serverHost"));
        log.setServiceName((String) msgPayload.get("serverName"));
        log.setTime(new Date((Long) msgPayload.get("time")));
        logDao.saveLog(log);

        return null;
    }
}
