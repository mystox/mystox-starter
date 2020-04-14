package com.kongtrolink.framework.scloud.api;

import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
import com.kongtrolink.framework.gateway.tower.core.constant.GatewayTonerOperate;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * Created by mystoxlol on 2019/9/9, 12:55.
 * company: kongtrolink
 * description:
 * update record:
 */
@Register
public interface TerminalCommandService {


    /**
     *  SC向GW请求监控点数据
     */
    @OperaCode(code = ScloudBusinessOperate.LOGIN)
    String login(String message);
    /**
     *  SC向GW请求监控点数据
     */
    @OperaCode(code = ScloudBusinessOperate.LOGIN_OFFINE)
    String loginOffline(String message);
}
