package com.kongtrolink.framework.reports.api;

import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.entity.ReportData;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/27 14:15
 * \* Description:
 * \
 */
@Register
public interface FsuReportsService {
    @OperaCode(code = OperaCodePrefix.REPORTS+"fsuOffLine")
    public ReportData fsuOffLine(String reportConfigStr);
    @OperaCode(code = OperaCodePrefix.REPORTS+"fsuRunning")
    public ReportData fsuRunning(String reportConfigStr);
    @OperaCode(code = OperaCodePrefix.REPORTS+"fsuOffLineDetails")
    public ReportData fsuOffLineDetails(String reportConfigStr);


}