package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.reports.api.ReportsService;
import com.kongtrolink.framework.reports.entity.DataType;
import com.kongtrolink.framework.reports.entity.ExecutorType;
import com.kongtrolink.framework.reports.entity.ReportConfig;
import com.kongtrolink.framework.reports.entity.ReportData;
import com.kongtrolink.framework.reports.service.ReportsHandlerAdapter;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/23, 9:38.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ReportsServiceImpl extends ReportsHandlerAdapter implements ReportsService {


    @Override
    @ReportOperaCode(code = OperaCodePrefix.REPORTS + "config", rhythm = 20, resultType = {DataType.JSON}, extend =
            {
                    @ReportExtend(field = "date", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要
                    @ReportExtend(field = "startTime", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要(查询用)
                    @ReportExtend(field = "endTime", name = "时间", type = ReportExtend.FieldType.DATE), //时间类型是否需要（查询用）
                    @ReportExtend(field = "region", name = "区域", type = ReportExtend.FieldType.DISTRICT)
            })
    public ReportData reportConfig(String reportConfigStr) {
        System.out.println("receive task");
        System.out.println(reportConfigStr);
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        ExecutorType executorType = reportConfig.getExecutorType();
        if (ExecutorType.query.equals(executorType))
            return query(reportConfig);
        else return executor(reportConfig);
    }

    @Override
    protected ReportData executor(ReportConfig reportConfig) {
        System.out.println("executor.......");
        try {
            Thread.sleep(40000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String jsonResult = "{\"name\":\"示例报表\",\"unit\":\"\",\"data\":[[\"6.66\",\"3.07\",\"2.79\",\"5.45\",\"6.31\",\"3.49\",\"2.37\",\"4.23\"],[\"2.66\",\"1.42\",\"4.10\",\"3.58\",\"4.76\",\"8.67\",\"8.19\",\"6.52\"],[\"6.53\",\"8.48\",\"3.76\",\"2.45\",\"5.10\",\"5.46\",\"9.74\",\"2.74\"],[\"4.52\",\"7.76\",\"6.30\",\"9.65\",\"9.14\",\"6.42\",\"7.30\",\"5.37\"],[\"5.98\",\"8.88\",\"9.63\",\"9.02\",\"1.49\",\"0.01\",\"8.45\",\"7.63\"],[\"9.08\",\"3.29\",\"6.97\",\"9.54\",\"4.05\",\"6.62\",\"0.21\",\"8.93\"],[\"6.47\",\"0.19\",\"2.50\",\"7.48\",\"5.94\",\"8.68\",\"9.52\",\"2.27\"]],\"xAxis\":[\"类目1\",\"类目2\",\"类目3\",\"类目4\",\"类目5\",\"类目6\",\"类目7\",\"类目8\"]}";
        ReportData reportData = new ReportData(DataType.JSON, jsonResult);
        System.out.println("executor.......end ");
        return reportData;
    }

    @Override
    protected ReportData query(ReportConfig reportConfig) {
        //查询
        System.out.println("query.......");
        String jsonResult = "{\"name\":\"示例报表\",\"unit\":\"\",\"data\":[[\"6.66\",\"3.07\",\"2.79\",\"5.45\",\"6.31\",\"3.49\",\"2.37\",\"4.23\"],[\"2.66\",\"1.42\",\"4.10\",\"3.58\",\"4.76\",\"8.67\",\"8.19\",\"6.52\"],[\"6.53\",\"8.48\",\"3.76\",\"2.45\",\"5.10\",\"5.46\",\"9.74\",\"2.74\"],[\"4.52\",\"7.76\",\"6.30\",\"9.65\",\"9.14\",\"6.42\",\"7.30\",\"5.37\"],[\"5.98\",\"8.88\",\"9.63\",\"9.02\",\"1.49\",\"0.01\",\"8.45\",\"7.63\"],[\"9.08\",\"3.29\",\"6.97\",\"9.54\",\"4.05\",\"6.62\",\"0.21\",\"8.93\"],[\"6.47\",\"0.19\",\"2.50\",\"7.48\",\"5.94\",\"8.68\",\"9.52\",\"2.27\"]],\"xAxis\":[\"类目1\",\"类目2\",\"类目3\",\"类目4\",\"类目5\",\"类目6\",\"类目7\",\"类目8\"]}";
        ReportData reportData = new ReportData(DataType.JSON, jsonResult);
        return reportData;

    }
}
