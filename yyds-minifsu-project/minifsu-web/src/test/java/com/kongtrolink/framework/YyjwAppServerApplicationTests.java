package com.kongtrolink.framework;

import com.kongtrolink.framework.dao.FsuDevicesDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YyjwAppServerApplicationTests {
	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	FsuDevicesDao fsuDevicesDao;
	@Test
	public void contextLoads() {
		List<String> fsuIds = new ArrayList<>();
		fsuIds.add("31200143800294");
		fsuIds.add("33010200010380005");
		Map<String,Integer> result = fsuDevicesDao.getFsuDeviceCount(fsuIds);
		System.out.println(result);
	}

}
