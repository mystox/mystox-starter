package com.kongtrolink.framework.execute.module.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;

/**
 * Created by mystoxlol on 2019/3/25, 10:34.
 * company: kongtrolink
 * description:
 * update record:
 */
public interface TerminalService
{
    JSONArray getDeviceList(ModuleMsg moduleMsg);

    JSONObject listFsu(ModuleMsg requestBody);

    JSONObject saveTerminal(ModuleMsg moduleMsg);

    JSONObject setTerminal(ModuleMsg moduleMsg);

    JSONObject terminalLogSave(ModuleMsg moduleMsg);

    JSONObject TerminalStatus(ModuleMsg moduleMsg);

    JSONObject getRunStates(ModuleMsg moduleMsg);

    JSONObject getTerminalPayloadLog(ModuleMsg moduleMsg);

    JSONObject unBind(ModuleMsg moduleMsg);
}
