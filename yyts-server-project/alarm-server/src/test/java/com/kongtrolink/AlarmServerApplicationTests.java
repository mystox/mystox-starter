package com.kongtrolink;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.enttiy.Alarm;
import com.kongtrolink.framework.service.AlarmService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlarmServerApplicationTests {

	@Autowired
	AlarmService alarmService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testMongo(){
		String uniqueCode = "meitainuo";
		String service = "zhyd";
		Alarm alarm = new Alarm();
		alarm.setState(Contant.USEING);
		alarm.setService(service);
		alarm.setUniqueCode(uniqueCode);
		alarm.setLevel("1");
		alarm.setValue(222);
		alarmService.save(alarm, uniqueCode + service);
	}
}
