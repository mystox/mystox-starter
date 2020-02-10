package com.kongtrolink.framework.business.scloud.his.api;

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

    @OperaCode(code = "scloudHistory")
    void scloudHistory(String message);

}
