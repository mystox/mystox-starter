package com.kongtrolink.framework.reports.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.entity.UnitHead;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.mqtt.util.SpringContextUtil;
import com.kongtrolink.framework.register.runner.RegisterRunner;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.reports.dao.ReportTaskDao;
import com.kongtrolink.framework.reports.dao.ReportTaskResultDao;
import com.kongtrolink.framework.reports.entity.*;
import com.kongtrolink.framework.reports.stereotype.ReportExtend;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import static com.kongtrolink.framework.common.util.MqttUtils.preconditionGroupServerCode;
import static com.kongtrolink.framework.common.util.MqttUtils.preconditionServerCode;
import static com.kongtrolink.framework.common.util.MqttUtils.preconditionSubTopicId;

/**
 * Created by mystoxlol on 2019/10/23, 8:33.
 * company: kongtrolink
 * description: 报表基础类 报表实现的继承流程
 * update record:
 */
@Aspect
@Component
//@DependsOn(value = "registerRunner")
@Order(3)
public class ReportsHandler implements ApplicationRunner {


    private Logger logger = LoggerFactory.getLogger(ReportsHandler.class);

    @Value("${server.version}")
    private String serverVersion;

    @Value("${server.name}")
    private String serverName;

    @Value("${server.groupCode}")
    private String groupCode;
    @Value("${report.count:10}")
    private int scheduleTaskCount;

    @Autowired
    MqttSender mqttSender;

    @Autowired
    @Lazy
    RegisterRunner registerRunner;
    @Autowired
    private ReportTaskDao reportTaskDao;

    @Autowired
    @Lazy
    ReportTaskResultDao reportTaskResultDao;

    @Autowired
    @Lazy
    private ThreadPoolTaskExecutor reportsExecutor;

    @Autowired
    @Lazy
    ServiceRegistry serviceRegistry;


    @Autowired
    ScheduledExecutorService reportsScheduled;

    @Autowired
    public void setReportTaskDao(ReportTaskDao reportTaskDao) {
        this.reportTaskDao = reportTaskDao;
    }


    @Around("@annotation(com.kongtrolink.framework.reports.stereotype.ReportOperaCode) && @annotation(reportOperaCode)")
    private Object reportsHandler(ProceedingJoinPoint joinPoint, ReportOperaCode reportOperaCode) {
//        String value = reportOperaCode.value();//此处配置额外的配置项

        ReportExtend[] extend = reportOperaCode.extend();
        String[] resultTypes = reportOperaCode.dataType();
        List<ReportExtendProperties> extendPropertiesList = new ArrayList<>();
        for (ReportExtend reportExtend : extend) {
            ReportExtendProperties e = new ReportExtendProperties();
            String field = reportExtend.field();
            e.setField(field);
            String name = reportExtend.name();
            e.setName(name);
            ReportExtend.FieldType type = reportExtend.type();
            e.setType(type.name());
            e.setBelongs(reportExtend.belong().name());
            e.setHide(reportExtend.hide());
            e.setSelect(Arrays.asList(reportExtend.select()));
            e.setValue(reportExtend.value());
            e.setUri(reportExtend.uri());
            e.setDescription(reportExtend.description());
            extendPropertiesList.add(e);
        }

        Object[] args = joinPoint.getArgs();
        String reportConfigStr = (String) args[0];
        ReportConfig reportConfig = JSONObject.parseObject(reportConfigStr, ReportConfig.class);
        Boolean skipAspect = reportConfig.getSkipAspect();
        if (skipAspect != null && skipAspect) {
            try {
                Object proceed = joinPoint.proceed(args);
                return proceed;
                //入库
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        if (ExecutorType.query.equals(reportConfig.getExecutorType())) {
            logger.debug("reports request query and return");
            try {
                ReportData result = queryExecutor(joinPoint, args);
                return result;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return null;
            }
        } else {
            Integer rhythm = reportConfig.getRhythm();
            if (rhythm == null || rhythm == 0)
                rhythm = reportOperaCode.rhythm();
            ReportTask reportTask = taskController(reportConfig, extendPropertiesList, rhythm, resultTypes);
            //查找上一次的结果返回
            if (reportTask == null) return null;
            String taskId = reportTask.getId();
            if (StringUtils.isNotBlank(taskId)) {
                ReportTaskResult newByTaskId = reportTaskResultDao.findNewByTaskId(reportTask.getId());
                if (newByTaskId != null)
                    return newByTaskId.getResult();
            }
        }
        return null;
    }


    /**
     * 查询的执行
     *
     * @param joinPoint
     * @param args
     * @return
     */
    ReportData queryExecutor(ProceedingJoinPoint joinPoint, Object[] args) {
        try {
            Object data = joinPoint.proceed(args);
            ReportData result = null;
            if (data instanceof ReportData)
                result = (ReportData) data;
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


    private synchronized void resultSave(ReportTask reportTask, ReportData reportData) {
        Date recordTime = new Date();
        reportTask.setEndTime(recordTime);
        Date startTime = reportTask.getStartTime();
        ReportTask task = reportTaskDao.findByByUniqueCondition(
                reportTask.getServerCode(), reportTask.getEnterpriseCode(),
                reportTask.getOperaCode(), preconditionServerCode(serverName, serverVersion));
        String reportTaskId = reportTask.getId();
        int taskStatus = task.getTaskStatus();
        Date currentStartTime = task.getStartTime();
        if (!currentStartTime.equals(startTime)) {
            logger.warn("[{}]current task startTime[{}] was changed [{}], this task has been executed...", reportTaskId, startTime, currentStartTime);
            return;
        }
        if (TaskStatus.INVALID.getStatus() == taskStatus) {
            reportTask.setTaskStatus(TaskStatus.INVALID.getStatus());
            logger.warn("[{}]task use to be invalid...", reportTaskId);
        } else {
            reportTask.setTaskStatus(TaskStatus.VALID.getStatus());
        }
        Integer rhythm = reportTask.getRhythm();
        if (reportData != null && !DataType.ERROR.equals(reportData.getDataType())) {
            Date nextStartTime = reportData.getNextStartTime();
            if (nextStartTime != null)
                reportTask.setStartTime(reportData.getNextStartTime());
            else {
                //如果下一次执行时间为空，则设置节拍周期为下一次开始时间
//                reportTask.setStartTime(new Date(System.currentTimeMillis() + (rhythm == 0 ? 1 : rhythm) * 1000));
//                reportTask.setStartTime(new Date(startTime.getTime() + (rhythm == 0 ? 1 : rhythm) * 1000));
                reportTask.setStartTime(new Date(setNextStartTime(System.currentTimeMillis(),startTime.getTime(), rhythm)));
            }
            logger.debug("[{}]next task start time is [{}]", reportTaskId, reportTask.getStartTime());
        } else {//如果结果为空或者结果类型为ERROR，说明任务执行失败，则以定时器三倍执行周期都为错误为超时
            logger.error("[{}]task result[{}] is wrong", task.getId(), JSONObject.toJSONString(reportData));
//            Date startTime = reportTask.getStartTime();
            if (rhythm != null)
                if (System.currentTimeMillis() - startTime.getTime() > 1000 * 3 * (rhythm == 0 ? 1 : rhythm)) {
                    logger.error("[{}]task time out... set [invalid]...", reportTaskId);
                    reportTask.setTaskStatus(TaskStatus.TIMEOUT.getStatus());
                }
        }
        if (TaskType.singleTask.name().equals(reportTask.getTaskType())) //如果为单次任务，则在任务完成后设置任务为无效
        {
            logger.warn("[{}]task type is singleTask, set [invalid] when task ending...", reportTaskId);
            reportTask.setTaskStatus(TaskStatus.INVALID.getStatus());
        }

        reportTaskDao.save(reportTask);
        ReportTaskResult reportTaskResult = new ReportTaskResult();
        reportTaskResult.setRecordTime(recordTime);
        reportTaskResult.setRunId(System.currentTimeMillis() + "");
        reportTaskResult.setStartTime(startTime);
        reportTaskResult.setTaskId(reportTaskId);
        if (reportData != null)
            reportTaskResult.setResult(JSONObject.parseObject(JSONObject.toJSONString(reportData), ReportData.class));
        reportTaskResultDao.save(reportTaskResult);
    }


    protected ReportTask taskController(ReportConfig reportConfig, List<ReportExtendProperties> extend, Integer rhythm, String[] resultTypes) {
        String serverCode = reportConfig.getServerCode();
        String enterpriseCode = reportConfig.getEnterpriseCode();
        String operaCode = reportConfig.getOperaCode();
        ReportTask reportTask = new ReportTask();
        Boolean validity = reportConfig.getValidity();
        Long startTime = reportConfig.getStartTime();
        startTime = startTime == null ? System.currentTimeMillis() :
                (startTime < System.currentTimeMillis() ? System.currentTimeMillis() : startTime);//如果配置的生效时间早于当前时间，则设置当前时间
        boolean isTaskExists = reportTaskDao.isExistsByOperaCode(serverCode, enterpriseCode, operaCode, preconditionServerCode(serverName, serverVersion));
        if (!isTaskExists) {
            //生成任务
            reportTask.setOperaCode(operaCode);
            reportTask.setServerCode(serverCode);
            reportTask.setEnterpriseCode(enterpriseCode);
            ExecutorType executorType = reportConfig.getExecutorType();
            if (executorType != null) reportTask.setExecutorType(executorType);
            Long operaValidity = reportConfig.getOperaValidity();
            if (operaValidity != null) reportTask.setOperaValidity(operaValidity);
            String reportType = reportConfig.getReportType();
            reportTask.setTaskType(reportType);
            reportTask.setReportName(reportConfig.getReportName());
            reportTask.setStartTime(new Date(startTime));
            reportTask.setReportServerCode(preconditionServerCode(serverName, serverVersion));

            //触发任务

        } else {
            //修改任务
            reportTask = reportTaskDao.findByByUniqueCondition(serverCode, enterpriseCode, operaCode,
                    preconditionServerCode(serverName, serverVersion));
            ExecutorType executorType = reportConfig.getExecutorType();
            if (executorType != null) reportTask.setExecutorType(executorType);
            Long operaValidity = reportConfig.getOperaValidity();
            if (operaValidity != null) reportTask.setOperaValidity(operaValidity);
            String reportType = reportConfig.getReportType();
            reportTask.setTaskType(reportType);
            reportTask.setReportName(reportConfig.getReportName());
            reportTask.setStartTime(new Date(startTime));

        }
        if (validity != null && validity) {
            int taskStatus = reportTask.getTaskStatus();
            if (TaskStatus.RUNNING.getStatus() == taskStatus) {
                logger.warn("[{}]running task stay running", reportTask.getId());
            } else {
                reportTask.setTaskStatus(TaskStatus.VALID.getStatus());
            }
        } else {
            reportTask.setTaskStatus(TaskStatus.INVALID.getStatus());
        }
        reportTask.setCondition(reportConfig.getCondition());
        reportTask.setExtendProperties(extend);
        reportTask.setResultTypes(new ArrayList<>(Arrays.asList(resultTypes)));
        reportTask.setRhythm(rhythm);
        reportTaskDao.save(reportTask);
        return reportTask;


    }


    /**
     * 报表规定的返回值必须为reportData
     *
     * @param unit
     * @param reportConfigStr
     * @return
     */
    ReportData localExecute(String unit, String reportConfigStr) {
        String[] entity = unit.replace(UnitHead.LOCAL, "").split("/");
        String className = entity[0];
        String methodName = entity[1];
        ReportData result = null;
        try {
            Class<?> clazz = Class.forName(className);
            Object bean = SpringContextUtil.getBean(clazz);
            Method method = clazz.getDeclaredMethod(methodName, String.class);
            Object invoke = method.invoke(bean, reportConfigStr);
            if (invoke instanceof ReportData) {
                result = (ReportData) invoke;
            }
            return result;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ReportConfig buildScheduledReportConfig(ReportTask reportTask) {

        ReportConfig reportConfig = new ReportConfig();
        BeanUtils.copyProperties(reportTask, reportConfig);
        reportConfig.setReportType(reportTask.getTaskType());
        reportConfig.setSkipAspect(true);
        return reportConfig;
    }

    private boolean taskInvalid(Long operaValidity) {
        return operaValidity > System.currentTimeMillis() || operaValidity == -1;
    }


    void task() {
        try {
            ReportTask reportTask = reportTaskDao.findExecuteReportTask(preconditionServerCode(serverName, serverVersion));
            if (reportTask == null) return;
            String reportTaskId = reportTask.getId();
            logger.debug("[{}]task executor..", reportTaskId);
            Integer rhythm = reportTask.getRhythm();
            Long operaValidity = reportTask.getOperaValidity();
            // 超时或设置无效
            if (!taskInvalid(operaValidity)) {
                logger.warn("[{}]task invalidity...", reportTaskId);
                reportTask.setTaskStatus(TaskStatus.INVALID.getStatus());
                reportTask.setEndTime(new Date());
                reportTaskDao.save(reportTask);
                return;
            }
            FutureTask<ReportData> futureTask = new FutureTask<>(() -> {
                ReportData result = null;
                ReportConfig reportConfig = buildScheduledReportConfig(reportTask);
                String operaCode = reportConfig.getOperaCode();
                String data = serviceRegistry.getData(
                        preconditionSubTopicId(preconditionGroupServerCode(groupCode,
                                preconditionServerCode(this.serverName, this.serverVersion)),
                                operaCode));
                RegisterSub sub = JSONObject.parseObject(data, RegisterSub.class);
                String executeUnit = sub.getExecuteUnit();
                if (executeUnit.startsWith(UnitHead.LOCAL)) { //执行本地函数和方法
                    result = localExecute(executeUnit, JSONObject.toJSONString(reportConfig));
                    return result;
                } else if (executeUnit.startsWith(UnitHead.JAR)) {//亦可执行本地和远程的jar，远程可执行jar以仓库的方式开放。
                    //                        result = jarExecute(executeUnit, JSONObject.toJSONString(reportConfig));
                } else if (executeUnit.startsWith(UnitHead.HTTP)) {
                    //todo 执行远程的http服务器
                }
                return new ReportData("error", "execute invalidity");
            });
//            futureTask.run();
            ReportData result = null;
            try {
                reportsExecutor.execute(futureTask);
                result = futureTask.get(rhythm * 3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                result = new ReportData(DataType.ERROR, "execute invalidity" + e.toString());
                e.printStackTrace();
            } catch (ExecutionException | TimeoutException e) {
                e.printStackTrace();
                result = new ReportData(DataType.ERROR, "execute invalidity" + e.toString());
            }
            //保存执行结果
            resultSave(reportTask, result);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        //启动任务执行器
        for (int i = 0; i < 9; i++)
            reportsScheduled.scheduleWithFixedDelay(this::task
                    , 1, 1, TimeUnit.SECONDS);

        //启动任务扫描器
        final Long serverStartTimeStamp = System.currentTimeMillis();
        reportsScheduled.scheduleWithFixedDelay(() -> checkRunning(serverStartTimeStamp)
                , 1, 3, TimeUnit.SECONDS);

    }

    private void checkRunning(Long serverStartTimeStamp) {
        // 检查正在运行的任务是否超时
//        logger.debug("check running report task...");
        List<ReportTask> reportTasks = reportTaskDao.findRunningReportTask(preconditionServerCode(serverName, serverVersion));
        if (CollectionUtils.isEmpty(reportTasks)) return;
        for (ReportTask reportTask : reportTasks) {
            Date startTime = reportTask.getStartTime();
            Integer rhythm = reportTask.getRhythm();
            String reportTaskId = reportTask.getId();
            long currentTimeMillis = System.currentTimeMillis();
            long timeout3 = rhythm * 1000 * 3;
            //服务启动时处理被中断的任务
            if (serverStartTimeStamp > startTime.getTime() && serverStartTimeStamp - startTime.getTime() > rhythm * 1000) {
                reportTask.setTaskStatus(TaskStatus.VALID.getStatus());
                Date newStartTime = new Date(setNextStartTime(currentTimeMillis, startTime.getTime(), rhythm));
                reportTask.setStartTime(newStartTime);
                logger.warn("[{}]check running task restart...startTime is [{}]", reportTaskId,newStartTime);
            }
            if (currentTimeMillis - startTime.getTime() > timeout3) { //如果任务三个周期内未改变运行状态，则该任务超时失效
                logger.warn("[{}]check running task timeout 3 multiple rhythm[{}]", reportTaskId, rhythm);
                reportTask.setTaskStatus(TaskStatus.TIMEOUT.getStatus());
            }/* else if (currentTimeMillis - startTime.getTime() > rhythm * 1000) { //如果任务一个周期外，未改变任务状态，则该任务为一个周期超时，将任务状态置为有效，但不改变任务开始时间, 此举是为解决服务重启带来的任务中断的超时问题
                logger.warn("[{}]check running task timeout 1 multiple rhythm[{}]", reportTaskId, rhythm);
                reportTask.setTaskStatus(TaskStatus.VALID.getStatus());
                reportTask.setStartTime(new Date());
            }*/

            reportTaskDao.save(reportTask);

        }


    }

    long setNextStartTime(long currentTimeMillis, long startTime, int rhythm) {
        if (startTime > currentTimeMillis) return startTime; //获取服务启动时间的前一个节拍周期
        return setNextStartTime(currentTimeMillis, startTime + (rhythm == 0 ? 1 : rhythm) *1000, rhythm);
    }
}
