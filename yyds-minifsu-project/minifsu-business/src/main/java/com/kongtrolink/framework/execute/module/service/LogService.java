package com.kongtrolink.framework.execute.module.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.core.entity.ModuleMsg;

/**
 * Created by mystoxlol on 2019/3/29, 16:30.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface LogService {
    JSONObject saveLog(ModuleMsg moduleMsg);

    /**
     * 统一保存日志接口
     * @param log
     */
    void saveLog(Log log);
}
