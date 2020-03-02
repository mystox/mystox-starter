package com.kongtrolink.framework.scloud.entity;

import java.util.Date;

/**
 * 定时任务 保存
 * 
 * @author John
 *
 */
public class JobEntity {
	
	public static final String namespace = "quartz_job_detail";
	public static final String taskClassPath = "com.kongtrolink.framework.scloud.job.";
	
	private String id;// 主键ID
	private String jobDescript;//任务描述
	private String jobName;// 任务名
	private String jobGroupName; // 任务组名  --给与默认
	private String triggerName; // 触发器名 --给与默认
	private String triggerGroupName; // 触发器组名 --给与默认
	private String jobClass; // 任务 类
	private String cron;// 时间设置，参考quartz说明文档
	private Date lastTime;// 最新一次执行时间
	private Date firstTime;// 第一次执行时间
	private String status;// 运行状态 停止 / 启动
	private String uniqueCode;//企业编码

	public JobEntity(){
		
	}
	public JobEntity(String jobName, String className, String cron,String uniqueCode) {
		this(jobName,className,cron,"启动",uniqueCode);
	}

	public JobEntity(String jobName, String className, String cron, String status,String uniqueCode) {
		//默认 加载 com.kongtrolink.scloud.job 包下的 类 
		this(jobName,jobName,taskClassPath+className,cron,status,uniqueCode);
	}
	
	public JobEntity(String jobName, String jobDescript, String jobClass, String cron, String status,String uniqueCode) {
		this.jobDescript= jobDescript;
		this.jobName = jobName;
		this.jobGroupName = jobName+"Group";
		this.triggerName = jobName+"Trigger";
		this.triggerGroupName = jobName+"TriggerGroup";
		this.jobClass = jobClass;//特别需要注意**** 需要完整 路径 包名+类名
		this.cron = cron;
		this.lastTime= new Date();
		this.status = status;
		this.uniqueCode = uniqueCode;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroupName() {
		return jobGroupName;
	}

	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroupName() {
		return triggerGroupName;
	}

	public void setTriggerGroupName(String triggerGroupName) {
		this.triggerGroupName = triggerGroupName;
	}

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public Date getFirstTime() {
		return firstTime;
	}

	public void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getJobDescript() {
		return jobDescript;
	}

	public void setJobDescript(String jobDescript) {
		this.jobDescript = jobDescript;
	}
	
	public JobEntity clone(){
		JobEntity entity = new JobEntity();
		entity.setCron(this.cron);
		entity.setFirstTime(this.firstTime);
		entity.setJobClass(this.jobClass);
		entity.setJobDescript(this.jobDescript);
		entity.setJobName(this.jobName);
		entity.setJobGroupName(this.jobGroupName);
		entity.setTriggerName(this.triggerName);
		entity.setTriggerGroupName(this.triggerGroupName);
		entity.setLastTime(this.lastTime);
		entity.setStatus(this.status);
		entity.setUniqueCode(this.uniqueCode);
		return entity;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}
}
