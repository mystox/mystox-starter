package com.kongtrolink.framework.execute.module.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;

/**
 * Created by mystoxlol on 2019/4/1, 19:30.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface AlarmService {

    /**
     * @auther: liudd
     * @date: 2019/4/1 20:35
     * 功能描述:根据告警点id添加或修改告警
     */
    JSONObject save(ModuleMsg moduleMsg);
}
