package com.kongtrolink.framework.execute.module.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;

/**
 * Created by mystoxlol on 2019/4/10, 18:57.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface DataMntService {
    JSONObject getSignalList(ModuleMsg moduleMsg);

    JSONObject setThreshold(ModuleMsg moduleMsg);
}