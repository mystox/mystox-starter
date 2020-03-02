package com.kongtrolink.framework.scloud.entity;


import java.util.Date;

/**
 * 修改 定时任务的 日志保存
 * @author John
 *
 */
public class JobUpdateLogEntity {
	
	public static final String namespace="quartz_update_log";
	
	private String id;//主键ID
	private String type;//修改类型。 00=新增 ;01=启动 10=停止 11=删除 状态的修改。 30 = 时间表达式的修改
	private String jobId;//修改的Job ID
	private JobEntity beforeJob;//修改前数据
	private JobEntity afterJob;//修改后数据
	private Date utime;//修改时间
	
	public JobUpdateLogEntity(){}
	
	public JobUpdateLogEntity( String type){
		this(type,null,null,null,new Date());
	}
	
	public JobUpdateLogEntity( String type, String jobId, JobEntity beforeJob, JobEntity afterJob,
			Date utime) {
		this.type = type;
		this.jobId = jobId;
		this.beforeJob = beforeJob;
		this.afterJob = afterJob;
		this.utime = utime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public JobEntity getBeforeJob() {
		return beforeJob;
	}
	public void setBeforeJob(JobEntity beforeJob) {
		this.beforeJob = beforeJob;
	}
	public JobEntity getAfterJob() {
		return afterJob;
	}
	public void setAfterJob(JobEntity afterJob) {
		this.afterJob = afterJob;
	}
	public Date getUtime() {
		return utime;
	}
	public void setUtime(Date utime) {
		this.utime = utime;
	}
	
	
	
}
