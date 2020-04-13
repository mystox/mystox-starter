package com.kongtrolink.framework.log.api;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * Created by mystoxlol on 2019/10/14, 16:08.
 * company: kongtrolink
 * description:
 * update record:
 */
@Register
public interface LogService {
    @OperaCode
    public void mqLog(String moduleMsgStr);


}
