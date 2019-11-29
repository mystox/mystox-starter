package com.kongtrolink;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.EnumLevelName;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import jdk.nashorn.internal.ir.EmptyNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlarmServerWebApplicationTests {

	@Autowired
	MongoTemplate mongoTemplate;
	@Test
	public void contextLoads() {
		Date curDate = new Date();
		EnterpriseLevel enterpriseLevel = new EnterpriseLevel();
		enterpriseLevel.setId(null);
		enterpriseLevel.setCode("system_code");
		enterpriseLevel.setName("系统默认告警等级2222");
		int level = 1;
		enterpriseLevel.setLevel(level);
		enterpriseLevel.setLevelName(EnumLevelName.getNameByLevel(level));
		enterpriseLevel.setColor("#DB001B");
		enterpriseLevel.setUpdateTime(curDate);
		enterpriseLevel.setState(Contant.USEING);
		mongoTemplate.save(enterpriseLevel, MongTable.ENTERPRISE_LEVEL);
	}

	@Test
	public void testList(){

	}

}
