package com.kongtrolink.framework.reports.api;

import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.entity.ReportData;
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


    @OperaCode(code = OperaCodePrefix.REPORTS+"config") //属于报表逻辑的operaCode约定前缀为report_
    public ReportData reportConfig(String reportConfigStr);

    @OperaCode(code = OperaCodePrefix.REPORTS+"report1") //属于报表逻辑的operaCode约定前缀为report_
    public ReportData report1(String reportConfigStr);





}
