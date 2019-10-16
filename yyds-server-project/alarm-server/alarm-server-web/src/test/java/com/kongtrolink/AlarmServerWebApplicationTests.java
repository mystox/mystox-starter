package com.kongtrolink;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.MongTable;
import com.kongtrolink.framework.enttiy.EnterpriseLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlarmServerWebApplicationTests {

	@Autowired
	MongoTemplate mongoTemplate;
	@Test
	public void contextLoads() {

		EnterpriseLevel enterpriseLevel = new EnterpriseLevel();
		enterpriseLevel.setName("默认企业告警等级");
		enterpriseLevel.setLevels(Arrays.asList("一", "二", "三", "四", "五", "六", "七", "八"));
		enterpriseLevel.setLevelNames(Arrays.asList("一级告警", "二级告警", "三级告警", "四级告警", "五级告警", "六级告警", "七级告警", "八级告警"));
		enterpriseLevel.setColors(Arrays.asList("#993838", "#993838", "#993838", "#993838", "#993838", "#993838", "#993838", "#993838"));
		enterpriseLevel.setDefaultLevel(Contant.YES);
		mongoTemplate.save(enterpriseLevel, MongTable.ENTERPRISE_LEVEL);
	}

}
