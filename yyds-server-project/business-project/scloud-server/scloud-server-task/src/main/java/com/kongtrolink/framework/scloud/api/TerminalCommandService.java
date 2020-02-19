package com.kongtrolink.framework.scloud.api;

import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
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

    @OperaCode(code = ScloudBusinessOperate.UPDATE_FSU_POLLING)
    String updateFsuPolling(String message);

}
