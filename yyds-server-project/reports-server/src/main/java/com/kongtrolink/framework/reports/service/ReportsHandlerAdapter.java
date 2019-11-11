package com.kongtrolink.framework.reports.service;

import com.kongtrolink.framework.reports.entity.ReportConfig;
import com.kongtrolink.framework.reports.entity.ReportData;

/**
 * Created by mystoxlol on 2019/11/1, 17:10.
 * company: kongtrolink
 * description:
 * update record:
 */
public abstract class ReportsHandlerAdapter {
    protected abstract ReportData executor(ReportConfig reportConfig);

    protected abstract ReportData query(ReportConfig reportConfigStr);

}
