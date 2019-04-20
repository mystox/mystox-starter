package com.kongtrolink.framework.execute.module.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;

/**
 * Created by mystoxlol on 2019/3/27, 23:47.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface RegistryService
{
    JSONObject registerSN(ModuleMsg moduleMsg);

    JSONObject registerDevices(ModuleMsg moduleMsg);

    JSONObject registerTerminal(ModuleMsg moduleMsg);

    JSONObject saveCleanupLog(ModuleMsg moduleMsg);

    JSONObject terminalHeart(ModuleMsg moduleMsg);
}
