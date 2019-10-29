package com.kongtrolink.framework.reports.api;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * Created by mystoxlol on 2019/9/9, 12:55.
 * company: kongtrolink
 * description:
 * update record:
 */
@Register
public interface ReportsService  {


    @OperaCode
    public String reportConfig(String reportConfigStr);





}
