package com.kongtrolink.framework.reports.api;

import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.entity.ReportData;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * \* @Author: Mag
 * \* Date: 2020年4月8日 09:42:03
 * \* Description:
 * \ 首页需求：根据站点 按天统计历史告警数量
 */
@Register
public interface HomeAlarmReportsService {

    @OperaCode(code = OperaCodePrefix.REPORTS+"homeAlarmCount")
    public ReportData alarmCount(String reportConfigStr);
}