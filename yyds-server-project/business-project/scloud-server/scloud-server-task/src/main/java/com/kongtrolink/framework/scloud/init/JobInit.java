package com.kongtrolink.framework.scloud.init;


import com.kongtrolink.framework.scloud.entity.JobEntity;
import com.kongtrolink.framework.scloud.entity.JobUpdateLogEntity;
import com.kongtrolink.framework.scloud.service.JobLogService;
import com.kongtrolink.framework.scloud.service.JobService;
import com.kongtrolink.framework.scloud.util.QuartzManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 初始化 工具
 * @author John
 *
 */
@Service
public class JobInit {
	@Autowired
	QuartzManager quartzManager;
	@Autowired
	JobService jobService;
	
	@Autowired
	JobLogService jobLogService;
	/**
	 * 服务启动 加载 预设 跑批任务
	 * 任务JOb数  由 JobInitConfig 设定
	 * 任务Job详情 若 db存在数据 取db数据。
	 */
	/**
	 * 更新2018年1月4日 10:02:27
	 * task 定时任务 采用 spring-quartz xml配置形式 不进行动态配置时间粒度
	 */
	@PostConstruct
    public void  init(){  
		System.out.println("##服务开始 - 修改状态为 启动");
		//统一在数据库中进行配置启动
		List<JobEntity> dblist = JobInitConfig.getList();
//		List<JobEntity> dblist = jobService.findAll();
//		Map<String,JobEntity> jobMap = new HashMap<>();
		if(dblist!=null){
			for(JobEntity entity:dblist){
//				jobMap.put(entity.getJobName(),entity);
				quartzManager.addJob(entity);
			}
		}
//		for(JobEntity entity:list){
//			JobUpdateLogEntity longEntity = new JobUpdateLogEntity("01",entity.getId(),entity,entity,new Date());//任务 修改日志保存
//			String jobName = entity.getJobName();
//			if(jobMap.containsKey(jobName)){
//				quartzManager.addJob(jobMap.get(jobName));//定时任务 启动 配置信息调用数据库内容
//			}else{
//				quartzManager.addJob(entity);//定时任务 启动 配置信息调用初始化配置信息
//				jobService.addJob(entity);//数据库保存配置
//			}
//			jobLogService.addUpdateLog(longEntity);//定时任务 修改日志 保存
//		}
		jobService.updateAllStartOrStop("启动");//修改任务状态 为 启动状态
		System.out.println("##服务开始 - 启动 任务 成功");
    }
	
	@PreDestroy
    public void  dostory(){ 
		System.out.println("服务停止 - 修改状态为 停止");
		jobService.updateAllStartOrStop("停止");
    }
	 
	
}
