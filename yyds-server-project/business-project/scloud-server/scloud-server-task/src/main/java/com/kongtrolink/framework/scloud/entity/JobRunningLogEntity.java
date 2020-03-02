package com.kongtrolink.framework.scloud.entity;

import java.util.Date;

/**
 * 运行 时 日志
 * @author John
 *
 */
public class JobRunningLogEntity {
	
	public static final String namespace="quartz_running_log";
	
	private String id;//主键ID
	private String jobName;//任务名称
	private String descript;//任务描述
	private String className;//执行 class
	private String result;//执行结果
	private Date ctime;//执行 时间
	private String message;//备注消息
	
	public JobRunningLogEntity(){}
	
	public JobRunningLogEntity(String jobName, String descript,Date ctime) {
		this.jobName = jobName;
		this.descript = descript;
		this.ctime = ctime;
	}
	
	
	public JobRunningLogEntity(String jobName, String descript, String className, String result,String message, Date ctime) {
		this.jobName = jobName;
		this.descript = descript;
		this.className = className;
		this.result = result;
		this.ctime = ctime;
		this.message = message;
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
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Date getCtime() {
		return ctime;
	}
	public void setCtime(Date ctime) {
		this.ctime = ctime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
