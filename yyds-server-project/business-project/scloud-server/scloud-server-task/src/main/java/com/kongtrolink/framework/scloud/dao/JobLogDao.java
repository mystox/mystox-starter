package com.kongtrolink.framework.scloud.dao;


import com.kongtrolink.framework.scloud.entity.JobRunningLogEntity;
import com.kongtrolink.framework.scloud.entity.JobUpdateLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JobLogDao {
	@Autowired
	MongoTemplate mongoTemplate;

	
	public void insertRunningLog(JobRunningLogEntity entity) {
		mongoTemplate.save(entity, JobRunningLogEntity.namespace);
	}

	public void insertUpdateLog(JobUpdateLogEntity entity) {
		mongoTemplate.save(entity, JobUpdateLogEntity.namespace);
	}

}
