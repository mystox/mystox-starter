package com.kongtrolink.framework.gateway.tower.server.service.parse;

import com.chinatowercom.scservice.InvokeResponse;

/**
 * Created by mystoxlol on 2019/10/15, 15:40.
 * company: kongtrolink
 * description: 消息解析器
 * update record:
 */
public interface ParseService {
    InvokeResponse execute(String payload);
    void init();
    void configTransverterInit();

}
