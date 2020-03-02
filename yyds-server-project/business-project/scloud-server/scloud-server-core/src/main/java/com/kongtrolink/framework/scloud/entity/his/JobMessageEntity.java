package com.kongtrolink.framework.scloud.entity.his;
/**
 * 业务平台修改历史轮询粒度
 * @author John
 *
 */
public class JobMessageEntity {
	
	private String uniqueCode; //企业编码
	private String cron;//定时任务 时间粒度

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

}
