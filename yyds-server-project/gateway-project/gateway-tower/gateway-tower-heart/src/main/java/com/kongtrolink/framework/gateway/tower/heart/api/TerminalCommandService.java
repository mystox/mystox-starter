package com.kongtrolink.framework.gateway.tower.heart.api;

import com.kongtrolink.framework.gateway.tower.core.constant.GatewayTonerOperate;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * Created by mystoxlol on 2019/9/9, 12:55.
 * company: kongtrolink
 * description:
 * update record:
 * 心跳处理
 */
@Register
public interface TerminalCommandService {

    @OperaCode(code = GatewayTonerOperate.HEART)
    void towerFsuHeart(String message);

}
