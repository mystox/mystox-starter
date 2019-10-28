package com.kongtrolink.framework.reports.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.reports.dao.ReportTaskDao;
import com.kongtrolink.framework.reports.entity.ReportConfig;
import com.kongtrolink.framework.reports.entity.ReportTask;
import com.kongtrolink.framework.reports.entity.TaskStatus;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import com.kongtrolink.framework.service.MqttSender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/10/23, 8:33.
 * company: kongtrolink
 * description: 报表基础类 报表实现的继承流程
 * update record:
 */
@Aspect
@Component
public abstract class ReportsHandler {


    @Value("${server.version}")
    private String serverVersion;

    @Autowired
    MqttSender mqttSender;


    @Autowired
    private ReportTaskDao reportTaskDao;


    @Autowired
    private ThreadPoolTaskExecutor reportsExecutor;


    @Autowired
    public void setReportTaskDao(ReportTaskDao reportTaskDao) {
        this.reportTaskDao = reportTaskDao;
    }


    @Around("@annotation(com.kongtrolink.framework.reports.stereotype.ReportOperaCode) && @annotation(reportOperaCode)")
    private Object reportsHandler(ProceedingJoinPoint joinPoint, ReportOperaCode reportOperaCode) {
        String value = reportOperaCode.value();
        Object[] args = joinPoint.getArgs();
        String reportConfigStr = (String) args[1];
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        taskController(reportConfig);
        if (reportConfig.getAsyn()) {
            try {
                return joinPoint.proceed(args);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return "{'code':0}";
            }
        } else {
            reportsExecutor.execute(()->{
                try {
                    Object proceed = joinPoint.proceed(args);
//                    mqttSender.sendToMqtt();


                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }

         return "{'code':0}";
    }


    protected void taskController(ReportConfig reportConfig) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        String operaCode = reportConfig.getOperaCode();
        ReportTask reportTask = new ReportTask();
        boolean isTaskExists = reportTaskDao.isExistsByOperaCode(serverCode, enterpriseCode, operaCode, serverVersion);
        if (!isTaskExists) {
            //生成任务
            reportTask.setOperaCode(operaCode);
            reportTask.setServerCode(serverCode);
            reportTask.setEnterpriseCode(enterpriseCode);
            Boolean asyn = reportConfig.getAsyn();
            if (asyn != null) reportTask.setAsyn(asyn);
            Long operaValidity = reportConfig.getOperaValidity();
            if (operaValidity != null) reportTask.setOperaValidity(operaValidity);
            String reportType = reportConfig.getReportType();
            reportTask.setTaskType(reportType);
            reportTask.setReportName(reportConfig.getReportName());
            reportTask.setStartTime(new Date());
            reportTask.setReportServerVersion(serverVersion);
//            reportTask.setTaskStatus();
            reportTask.setTaskStatus(TaskStatus.VALID.getStatus());
//            reportTaskDao.save(reportTask);

            //触发任务

        } else {
            reportTask = reportTaskDao.findByByUniqueCondition(serverCode, enterpriseCode, operaCode, serverVersion);
            int taskStatus = reportTask.getTaskStatus();
            if (TaskStatus.RUNNING.getStatus() == taskStatus) { //正在运行的报表任务不能被修改
                return;
            } /*else if (TaskStatus.VALID.getStatus() == taskStatus) {
                //todo 更新任务

                //判断任务是否有效
            } else if (TaskStatus.INVALID.getStatus() == taskStatus) //无效任务改为有效任务
            {



            }*/
            Boolean asyn = reportConfig.getAsyn();
            if (asyn != null) reportTask.setAsyn(asyn);
            Long operaValidity = reportConfig.getOperaValidity();
            if (operaValidity != null) reportTask.setOperaValidity(operaValidity);
            String reportType = reportConfig.getReportType();
            reportTask.setTaskType(reportType);
            reportTask.setReportName(reportConfig.getReportName());
            reportTask.setStartTime(new Date());
            reportTask.setTaskStatus(TaskStatus.VALID.getStatus());

        }
            reportTaskDao.save(reportTask);


    }


}
