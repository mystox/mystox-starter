package com.kongtrolink.framework.scloud.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * FSU 历史数据查询
 */

public class HistoryTestTask implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryTestTask.class);

    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String uniqueCode = dataMap.getString("uniqueCode");
        LOGGER.info("企业:{} 开始进行心跳检测...",uniqueCode);
    }
}
