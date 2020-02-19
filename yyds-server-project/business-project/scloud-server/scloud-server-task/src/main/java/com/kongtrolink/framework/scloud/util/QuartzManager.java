package com.kongtrolink.framework.scloud.util;


import com.kongtrolink.framework.scloud.entity.JobEntity;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuartzManager {

	private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();

	/**
	 * @Description: 添加一个定时任务
	 * 
	 * @param jobName
	 *            任务名
	 * @param jobGroupName
	 *            任务组名
	 * @param triggerName
	 *            触发器名
	 * @param triggerGroupName
	 *            触发器组名
	 * @param jobClass
	 *            任务
	 * @param cron
	 *            时间设置，参考quartz说明文档
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class jobClass,
			String cron) {
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			// 任务名，任务组，任务执行类
			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroupName).build();

			// 触发器
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
			// 触发器名,触发器组
			triggerBuilder.withIdentity(triggerName, triggerGroupName);
			triggerBuilder.startNow();
			// 触发器时间设定
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
			// 创建Trigger对象
			CronTrigger trigger = (CronTrigger) triggerBuilder.build();

			// 调度容器设置JobDetail和Trigger
			scheduler.scheduleJob(jobDetail, trigger);

			// 启动
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * @Description: 添加一个定时任务
	 * @param entity
	 * jobName 任务名
	 *  cron 时间设置，参考quartz说明文档
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addJob(JobEntity entity) {
		String jobName = entity.getJobName();
		String cron = entity.getCron();
		String jobClassName = entity.getJobClass();
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			Class jobClass = Class.forName(jobClassName);
			// 任务名，任务组，任务执行类
			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobName + "Group").build();
			jobDetail.getJobDataMap().put("uniqueCode", entity.getUniqueCode());
			// 触发器
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
			// 触发器名,触发器组
			triggerBuilder.withIdentity(jobName + "Trigger", jobName + "TriggerGroup");
			triggerBuilder.startNow();
			// 触发器时间设定
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
			// 创建Trigger对象
			CronTrigger trigger = (CronTrigger) triggerBuilder.build();

			// 调度容器设置JobDetail和Trigger
			scheduler.scheduleJob(jobDetail, trigger);

			// 启动
			if (!scheduler.isShutdown()) {
				scheduler.start();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *  修改一个任务的触发时间

	 * @param triggerName
	 *            触发器名
	 * @param triggerGroupName
	 *            触发器组名
	 * @param cron
	 *            时间设置，参考quartz说明文档
	 */
	public void modifyJobTime(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
			String cron) {
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);// 从代码看
																							// 触发器名称与触发器组名构成了唯一键
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			if (trigger == null) {
				return;
			}

			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(cron)) {
				/** 方式一 ：调用 rescheduleJob 开始 */
				// 触发器
				TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
				// 触发器名,触发器组
				triggerBuilder.withIdentity(triggerName, triggerGroupName);
				triggerBuilder.startNow();
				// 触发器时间设定
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
				// 创建Trigger对象
				trigger = (CronTrigger) triggerBuilder.build();
				// 方式一 ：修改一个任务的触发时间
				scheduler.rescheduleJob(triggerKey, trigger);
				/** 方式一 ：调用 rescheduleJob 结束 */

				/** 方式二：先删除，然后在创建一个新的Job */
				// JobDetail jobDetail =
				// scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
				// Class<? extends Job> jobClass = jobDetail.getJobClass();
				// removeJob(jobName, jobGroupName, triggerName,
				// triggerGroupName);
				// addJob(jobName, jobGroupName, triggerName, triggerGroupName,
				// jobClass, cron);
				/** 方式二 ：先删除，然后在创建一个新的Job */
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Description: 修改一个任务的触发时间
	 * @param jobName
	 * @param cron
	 *            时间设置，参考quartz说明文档
	 */
	public void modifyJobTime(String jobName, String cron) {
		String triggerName = jobName + "Trigger";
		String triggerGroupName = jobName + "TriggerGroup";
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);// 从代码看
																							// 触发器名称与触发器组名构成了唯一键
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			if (trigger == null) {
				return;
			}

			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(cron)) {
				/** 方式一 ：调用 rescheduleJob 开始 */
				// 触发器
				TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
				// 触发器名,触发器组
				triggerBuilder.withIdentity(triggerName, triggerGroupName);
				triggerBuilder.startNow();
				// 触发器时间设定
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
				// 创建Trigger对象
				trigger = (CronTrigger) triggerBuilder.build();
				// 方式一 ：修改一个任务的触发时间
				scheduler.rescheduleJob(triggerKey, trigger);
				/** 方式一 ：调用 rescheduleJob 结束 */

				/** 方式二：先删除，然后在创建一个新的Job */
				// JobDetail jobDetail =
				// scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
				// Class<? extends Job> jobClass = jobDetail.getJobClass();
				// removeJob(jobName, jobGroupName, triggerName,
				// triggerGroupName);
				// addJob(jobName, jobGroupName, triggerName, triggerGroupName,
				// jobClass, cron);
				/** 方式二 ：先删除，然后在创建一个新的Job */
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *  修改一个任务的触发时间
	 * @param entity 时间设置，参考quartz说明文档
	 */
	public void modifyJobTime(JobEntity entity) {
		String jobName = entity.getJobName();
		String cron = entity.getCron();
		String triggerName = jobName + "Trigger";
		String triggerGroupName = jobName + "TriggerGroup";
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			if (trigger == null) {
				return;
			}

			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(cron)) {
				/** 方式一 ：调用 rescheduleJob 开始 */
				// 触发器
				TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
				// 触发器名,触发器组
				triggerBuilder.withIdentity(triggerName, triggerGroupName);
				triggerBuilder.startNow();
				// 触发器时间设定
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
				// 创建Trigger对象
				trigger = (CronTrigger) triggerBuilder.build();
				// 方式一 ：修改一个任务的触发时间
				scheduler.rescheduleJob(triggerKey, trigger);
				/** 方式一 ：调用 rescheduleJob 结束 */

				/** 方式二：先删除，然后在创建一个新的Job */
				// JobDetail jobDetail =
				// scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
				// Class<? extends Job> jobClass = jobDetail.getJobClass();
				// removeJob(jobName, jobGroupName, triggerName,
				// triggerGroupName);
				// addJob(jobName, jobGroupName, triggerName, triggerGroupName,
				// jobClass, cron);
				/** 方式二 ：先删除，然后在创建一个新的Job */
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Description: 移除一个任务
	 * 
	 * @param jobName
	 * @param jobGroupName
	 * @param triggerName
	 * @param triggerGroupName
	 */
	public void removeJob(String jobName,String triggerName,String triggerGroupName,String jobGroupName) {
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);

			scheduler.pauseTrigger(triggerKey);// 停止触发器
			scheduler.unscheduleJob(triggerKey);// 移除触发器
			scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));// 删除任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 移除一个任务
	 */
	public void removeJob(JobEntity entity) {
		String jobName = entity.getJobName();
		String triggerName = jobName + "Trigger";
		String triggerGroupName = jobName + "TriggerGroup";
		String jobGroupName = jobName + "Group";
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);

			scheduler.pauseTrigger(triggerKey);// 停止触发器
			scheduler.unscheduleJob(triggerKey);// 移除触发器
			scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));// 删除任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * @Description: 移除一个任务
	 * @param jobName
	 */
	public void removeJob(String jobName) {
		String triggerName = jobName + "Trigger";
		String triggerGroupName = jobName + "TriggerGroup";
		String jobGroupName = jobName + "Group";
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);

			scheduler.pauseTrigger(triggerKey);// 停止触发器
			scheduler.unscheduleJob(triggerKey);// 移除触发器
			scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));// 删除任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean ifJobExist(JobEntity entity) {
		String jobName = entity.getJobName();
		String triggerName = jobName + "Trigger";
		String triggerGroupName = jobName + "TriggerGroup";
		String jobGroupName = jobName + "Group";
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);// 从代码看
			JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, jobGroupName));
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			if (trigger == null||jobDetail==null) {
				return false;
			}else{
				return true;
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @Description:启动所有定时任务
	 */
	public void startJobs() {
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			scheduler.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Description:关闭所有定时任务
	 */
	public void shutdownJobs() {
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			if (!scheduler.isShutdown()) {
				scheduler.shutdown();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}