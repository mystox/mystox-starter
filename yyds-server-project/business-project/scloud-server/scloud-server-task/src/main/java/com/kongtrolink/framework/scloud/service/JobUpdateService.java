package com.kongtrolink.framework.scloud.service;

import com.alibaba.fastjson.JSON;
import com.kongtrolink.framework.scloud.entity.JobEntity;
import com.kongtrolink.framework.scloud.entity.his.JobMessageAckEntity;
import com.kongtrolink.framework.scloud.entity.his.JobMessageEntity;
import com.kongtrolink.framework.scloud.entity.JobUpdateLogEntity;
import com.kongtrolink.framework.scloud.util.QuartzManager;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class JobUpdateService {

	@Autowired
	QuartzManager quartzManager;
    
	@Autowired
	JobLogService jobLogService;
	
	@Autowired
	JobService jobService;
	

    /**
     * 定时任务 监听消息队列== 当收到消息后，自动调用该方法
     */
    public JobMessageAckEntity onMessage(JobMessageEntity entity) {
        try {
			String cron = entity.getCron();
			String uniqueCode = entity.getUniqueCode();
            JobUpdateLogEntity logEntity = new JobUpdateLogEntity("30");//存放 修改日志
            JobEntity queryEntity = new JobEntity(uniqueCode, "Task",cron,uniqueCode);
            JobEntity dbEntity = jobService.find(queryEntity);//根据job名称 从数据库中查找数据
            if(dbEntity!=null){
            	logEntity.setJobId(dbEntity.getId());
            	logEntity.setBeforeJob(dbEntity.clone());
        	}
            //修改时间粒度
			return updateJob(dbEntity,logEntity,entity);

        } catch (Exception e) {
            e.printStackTrace();
        }
		return new JobMessageAckEntity("设置异常");
    }

    /**
     * 30 修改 JOB 时间粒度
     */
    private JobMessageAckEntity updateJob(JobEntity dbEntity,JobUpdateLogEntity logEntity,JobMessageEntity entity){
    	try{
			String corn = entity.getCron();
			String uniqueCode = entity.getUniqueCode();
			boolean cronOk = CronExpression.isValidExpression(entity.getCron());//验证时间格式 是否完成
			if((dbEntity!=null && dbEntity.getCron().equals(corn))){
				return new JobMessageAckEntity("该任务设置与当前一致,无需修改.");
			}else if(!cronOk){
				return new JobMessageAckEntity("时间粒度不符合规范");
			}else{
				if(dbEntity==null){
					dbEntity = new JobEntity(uniqueCode, "HistoryTask",corn,uniqueCode);
				}
				boolean ifJobExist  =quartzManager.ifJobExist(dbEntity);//判断当前是否在运行
				if(!ifJobExist){
					dbEntity.setStatus("启动");
					logEntity.setAfterJob(dbEntity);
					quartzManager.addJob(dbEntity);
					jobService.addJob(dbEntity);
				}else{
					dbEntity.setCron(entity.getCron());
					logEntity.setAfterJob(dbEntity);
					quartzManager.modifyJobTime(dbEntity);
					jobService.update(dbEntity);
				}

			}
			return new JobMessageAckEntity();
		}catch (Exception e){
    		e.printStackTrace();
		}
		return new JobMessageAckEntity("设置异常");
    }


}