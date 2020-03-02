package com.kongtrolink.framework.scloud.dao;

import com.kongtrolink.framework.scloud.entity.JobEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class JobDao {
	@Autowired
    MongoTemplate mongoTemplate;

	
	public void insert(JobEntity entity) {
		mongoTemplate.save(entity, JobEntity.namespace);
	}

	public void update(JobEntity entity) {
		Update update = new Update();
		update.set("status", entity.getStatus());
		update.set("cron", entity.getCron());
		update.set("lastTime",new Date());
		Query query = new Query();
		Criteria cat = Criteria.where("_id").is(entity.getId());
		query.addCriteria(cat);
		mongoTemplate.updateFirst(query,update,JobEntity.class,JobEntity.namespace);
	}

	public void delete(JobEntity entity) {
		mongoTemplate.remove(
				new Query(Criteria.where("_id").is(entity.getId())),
				JobEntity.class, JobEntity.namespace);
	}

	public JobEntity find(JobEntity entity){
		return mongoTemplate.findOne(
				new Query(Criteria.where("triggerName").is(entity.getTriggerName()).and("triggerGroupName").is(entity.getTriggerGroupName())),
				JobEntity.class, JobEntity.namespace);
	}
	
	public List<JobEntity> findAll(){
		return mongoTemplate.findAll(JobEntity.class, JobEntity.namespace);
	}
	
	public void updateStop(String status){
		Update update = new Update();
		update.set("status",status);
		update.set("lastTime",new Date());
		mongoTemplate.updateMulti(new Query(), update, JobEntity.namespace);
	}
}
