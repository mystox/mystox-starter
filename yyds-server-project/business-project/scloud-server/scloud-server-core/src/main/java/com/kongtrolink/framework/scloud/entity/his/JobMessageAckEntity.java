package com.kongtrolink.framework.scloud.entity.his;
/**
 * 业务平台修改历史轮询粒度
 * @author John
 *
 */
public class JobMessageAckEntity {
	

	private String message;
	private boolean success;//操作是否成功

	public JobMessageAckEntity() {
		this.message = "设置成功";
		this.success = true;
	}

	public JobMessageAckEntity(String message) {
		this.message = message;
		this.success = false;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
