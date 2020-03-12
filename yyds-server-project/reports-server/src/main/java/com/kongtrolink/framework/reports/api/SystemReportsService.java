package com.kongtrolink.framework.reports.api;

import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.entity.ReportData;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/2 15:21
 * \* Description:
 * \
 */
@Register
public interface SystemReportsService {

    @OperaCode(code = OperaCodePrefix.REPORTS+"stationOffStatistics") //站点停电统计表S
    public ReportData stationOffStatistics(String reportConfigStr);
    @OperaCode(code = OperaCodePrefix.REPORTS+"stationOffDetails") //站点停电明细表
    public ReportData stationOffDetails(String reportConfigStr);
    @OperaCode(code = OperaCodePrefix.REPORTS+"stationBreak") //市电停电断站统计表
    public ReportData stationBreak(String reportConfigStr);
    @OperaCode(code = OperaCodePrefix.REPORTS+"electricCount") //用电量统计表
    public ReportData electricCount(String reportConfigStr);
    @OperaCode(code = OperaCodePrefix.REPORTS+"powerLoad") //用点负荷统计表
    public ReportData powerLoad(String reportConfigStr);
}