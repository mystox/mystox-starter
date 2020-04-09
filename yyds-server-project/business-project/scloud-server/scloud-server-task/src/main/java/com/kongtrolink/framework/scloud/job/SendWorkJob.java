package com.kongtrolink.framework.scloud.job;

import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.service.JobWorkService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * 派单任务，扫描alarmWorkConfig表
 */
@Service
public class SendWorkJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendWorkJob.class);
    @Autowired
    JobWorkService jobWorkService;

    public void execute(){
        LOGGER.info("扫描告警工单配置开始...");
        Date curDate = new Date();
        List<WorkAlarmConfig> allAlarmWorkConfig = jobWorkService.getAllWorkAlarmConfig(curDate);
        if(allAlarmWorkConfig == null || allAlarmWorkConfig.size() == 0){
            LOGGER.info("没有需要自动派单的告警");
            return ;
        }
        LOGGER.info("共有 {} 条符合条件的配置", allAlarmWorkConfig.size());
        for(WorkAlarmConfig alarmWorkConfig : allAlarmWorkConfig){
            handleAlarm(alarmWorkConfig, curDate);
        }
        LOGGER.info("扫描告警工单配置结束");
    }

    /**
     * 处理告警工单配置对应表
     * @param alarmWorkConfig
     */
    private void handleAlarm(WorkAlarmConfig alarmWorkConfig, Date curDate){
        String uniqueCode = alarmWorkConfig.getUniqueCode();
        //获取东单配置
        WorkConfig workConfig = jobWorkService.getWorkConfigById(uniqueCode, alarmWorkConfig.getWorkConfigId());
        //判定该告警所在设备是否有未回单的工单
        Work noOverWork = jobWorkService.getNoOverWorkByDeviceCode(uniqueCode, alarmWorkConfig.getDeviceCode());
        WorkAlarm workAlarm = alarmWorkConfig.createWorkAlarm();
        if(null != noOverWork){
            //向工单中添加告警信息并保存
            noOverWork.getWorkAlarmList().add(workAlarm);
            jobWorkService.updateWork(uniqueCode, noOverWork);
        }else{
            //生成一条工单
            Work work = createWork(alarmWorkConfig, workConfig, workAlarm, WorkConstants.SEND_TYPE_AUTO, curDate);
            jobWorkService.addWork(uniqueCode, work);
            //生成一条工单记录
            WorkRecord workRecord = createWorkRecord(work, null, WorkConstants.OPERATE_SEND,
                    null, work.getOperatorTime(), 0, WorkConstants.FTU_WEB, null);
            jobWorkService.addWorkRecord(uniqueCode, workRecord);
            //liuddtodo 发送工单推送
//            workJpushService.pushWork(uniqueCode, work, workRecord);
        }

        //删除该告警工单配置对应信息
        jobWorkService.deleteWorkAlarmConfigById(alarmWorkConfig.getId());
        //liuddtodo 修改告警的工单状态
//        alarmTaskMongo.updateWorkInfo(uniqueCode, alarm, workId, workCode, EnumWorkStatus.SENDED.getName());
    }

    private Work createWork(WorkAlarmConfig workAlarmConfig, WorkConfig workConfig, WorkAlarm workAlarm, String sentType, Date curDate){
        Work work = new Work();
        work.setCode(StringUtil.createCodeByDate(curDate));
        work.setSentTime(curDate);
        work.setOperatorTime(curDate);
        work.setSendType(sentType);
        //site信息
        work.setSite(new FacadeView(workAlarmConfig.getSiteCode(), workAlarmConfig.getSiteName()));
        //设备信息
        work.setDevice(new FacadeView(workAlarmConfig.getDeviceCode(), workAlarmConfig.getDeviceName()));
        work.setDeviceType(workAlarmConfig.getDeviceType());
        work.setState(WorkConstants.STATE_RECEIVE);
        work.initCSJDXX(curDate, workConfig);
        work.initCSHDXX(curDate, workConfig);
        work.increateAlarm(workAlarm);
        return work;
    }

    public WorkRecord createWorkRecord(Work work, FacadeView maintainer, String operateType,
                                       String operateDescribe, Date operateTime,
                                       float handleTime, String operateFTU, FacadeView receiver){
        WorkRecord workRecord = new WorkRecord();
        workRecord.setWorkId(work.getId());
        workRecord.setWorker(maintainer);
        workRecord.setOperateType(operateType);
        workRecord.setOperateDescribe(operateDescribe);
        workRecord.setOperateTime(operateTime);
        workRecord.setHandleTime(handleTime);
        workRecord.setOperateFTU(operateFTU);
        workRecord.setReceiver(receiver);
        return workRecord;
    }
}
