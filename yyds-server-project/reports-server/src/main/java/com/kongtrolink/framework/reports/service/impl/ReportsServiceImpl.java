package com.kongtrolink.framework.reports.service.impl;

import com.kongtrolink.framework.reports.api.ReportsService;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/23, 9:38.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ReportsServiceImpl/* extends ReportsHandler*/ implements ReportsService {


    @Override
    @ReportOperaCode(value = "date")
    public String reportConfig(String reportConfigStr) {


        //TODO 具体的任务逻辑
//        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
//
//        taskController(reportConfig);

        return "";
    }
}
