package com.kongtrolink.framework.scloud.init;


import com.kongtrolink.framework.scloud.entity.JobEntity;
import com.kongtrolink.framework.scloud.util.JonCron;

import java.util.ArrayList;
import java.util.List;

/**
 * 启动  任务配置
 * @author John
 *
 */
public class JobInitConfig {
	
	private static List<JobEntity> list = new ArrayList<JobEntity>();
	/**
	 * JobEntity 参数说明
	 * 1：TestQuartz3 							 ---任务名称-设定为 类名
	 * 2：测试3     								 ---任务描述
	 * 3：com.kongtrolink.scloud.job.TestQuartz3  ---任务所在类的完整路径 (统一放在com.kongtrolink.scloud.job包下 默认 可以不传)
	 * 4：0/4 * * * * ? 						 	 --- 时间粒度 （com.kongtrolink.scloud.mq.util.JonCron）加入了几个常用 时间粒度 可参考
	 */
	public static List<JobEntity> getList(){
		if(list!=null&&list.size()>0){
			return list;
		}

        list.add(new JobEntity("yytd", "HistoryTestTask", JonCron.everySe10,"yytd"));
        list.add(new JobEntity("dadongdong", "HistoryTestTask", JonCron.everySe15,"dadongdong"));
        list.add(new JobEntity("cntb", "HistoryTestTask", JonCron.everySe20,"cntb"));
        return list;
	}
}
