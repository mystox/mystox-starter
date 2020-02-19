package com.kongtrolink.framework.scloud.service.impl;


import com.kongtrolink.framework.scloud.dao.JobLogDao;
import com.kongtrolink.framework.scloud.entity.JobRunningLogEntity;
import com.kongtrolink.framework.scloud.entity.JobUpdateLogEntity;
import com.kongtrolink.framework.scloud.service.JobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobLogServiceImpl implements JobLogService {
	
	@Autowired
	JobLogDao dao;
	
	@Override
	public void addUpdateLog(JobUpdateLogEntity entity) {
		dao.insertUpdateLog(entity);
	}

	@Override
	public void addRunningLog(JobRunningLogEntity entity) {
		dao.insertRunningLog(entity);
	}

}
