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
    @Autowired
    LogDao logDao;





    @Override
    public JSONObject saveLog(ModuleMsg moduleMsg) {

        JSONObject msgPayload = moduleMsg.getPayload();
        Log log = JSONObject.toJavaObject(msgPayload, Log.class);
        //日志记录
        logDao.saveLog(log);
        JSONObject result = new JSONObject();
        result.put("result", StateCode.SUCCESS);
        return result;
    }
}
