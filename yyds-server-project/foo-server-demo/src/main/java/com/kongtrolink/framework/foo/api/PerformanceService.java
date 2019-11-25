package com.kongtrolink.framework.foo.api;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/22 10:27
 * \* Description:
 * \
 */
@Register
public interface PerformanceService {

    @OperaCode
    public void countStatistic(String msg);
    @OperaCode
    public long getCount();
    @OperaCode
    public long clearCount(String param);


}