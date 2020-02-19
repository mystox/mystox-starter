package com.kongtrolink.framework.scloud.service;


import com.kongtrolink.framework.scloud.entity.JobRunningLogEntity;
import com.kongtrolink.framework.scloud.entity.JobUpdateLogEntity;

/**
 * job 日志保存
 * @author John
 *
 */
public interface JobLogService {
		/**
		 * job 修改 日志保存
		 * @param entity 消息
		 */
		void addUpdateLog(JobUpdateLogEntity entity);
		
		/**
		 * job 运行结果 日志保存
		 * @param entity  消息
		 */
		void addRunningLog(JobRunningLogEntity entity);
		
}
