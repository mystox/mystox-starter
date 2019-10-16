package com.kongtrolink;

import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlarmServerWebApplicationTests {

	@Autowired
	MongoTemplate mongoTemplate;
	@Test
	public void contextLoads() {

		EnterpriseLevel enterpriseLevel = new EnterpriseLevel();
		enterpriseLevel.setEnterpriseCode("testEnter");
		enterpriseLevel.setServerCode("testServer");
		enterpriseLevel.setLevel("6666");
		mongoTemplate.save(enterpriseLevel, MongTable.ENTERPRISE_LEVEL);
	}

}
