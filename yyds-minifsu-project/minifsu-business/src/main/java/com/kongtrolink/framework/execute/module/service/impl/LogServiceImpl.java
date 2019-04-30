package com.kongtrolink.framework.execute.module.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.StateCode;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import com.kongtrolink.framework.execute.module.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/3/29, 16:31.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class LogServiceImpl implements LogService {

    private LogDao logDao;

    @Autowired
    public void setLogDao(LogDao logDao) {
        this.logDao = logDao;
    }

    @Override
    public JSONObject saveLog(ModuleMsg moduleMsg) {
        JSONObject msgPayload = moduleMsg.getPayload();
        Log log = JSONObject.toJavaObject(msgPayload, Log.class);
        JSONObject result = new JSONObject();
        try {
            //日志记录
            saveLog(log);
            result.put("result", StateCode.SUCCESS);
        } catch (Exception e) {
            result.put("result", StateCode.FAILED);
            e.printStackTrace();
        }
        return result;
    }

    public void saveLog(Log log) {
        //日志记录
        logDao.saveLog(log);
    }
}
