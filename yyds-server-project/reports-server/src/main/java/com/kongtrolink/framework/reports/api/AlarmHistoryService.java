package com.kongtrolink.framework.reports.api;

import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.entity.ReportData;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * \* @Author: mystox
 * \* Date: 2020/4/8 13:47
 * \* Description:
 * \
 */
@Register
public interface AlarmHistoryService {

    @OperaCode(code = OperaCodePrefix.REPORTS+"alarmReports")
    public ReportData alarmHistoryReports(String reportConfigStr);


}