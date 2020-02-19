package com.kongtrolink.framework.scloud.service.impl;


import com.kongtrolink.framework.scloud.dao.JobDao;
import com.kongtrolink.framework.scloud.entity.JobEntity;
import com.kongtrolink.framework.scloud.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

	@Autowired
	JobDao dao;
	
	@Override
	public void addJob(JobEntity entity) {
		dao.insert(entity);
	}

	@Override
	public void update(JobEntity entity) {
		dao.update(entity);
	}

	@Override
	public void deleteJob(JobEntity entity) {
		dao.delete(entity);
	}

	@Override
	public JobEntity find(JobEntity entity) {
		return dao.find(entity);
	}

	@Override
	public List<JobEntity> findAll() {
		return dao.findAll();
	}

	@Override
	public void updateAllStartOrStop(String status) {
		dao.updateStop(status);
	}


}
