package com.kongtrolink.framework.reports.api;

import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.entity.ReportData;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * \* @Author: mystox
 * \* Date: 2020/2/11 14:19
 * \* Description:
 * \
 */
@Register
public interface AlarmReportsService {

    @OperaCode(code = OperaCodePrefix.REPORTS+"alarmCount")
    public ReportData alarmCount(String reportConfigStr);

    @OperaCode(code = OperaCodePrefix.REPORTS+"alarmDetails")
    public ReportData alarmDetails(String reportConfigStr);

    @OperaCode(code = OperaCodePrefix.REPORTS+"alarmCategory")
    public ReportData alarmCategory(String reportConfigStr);
}