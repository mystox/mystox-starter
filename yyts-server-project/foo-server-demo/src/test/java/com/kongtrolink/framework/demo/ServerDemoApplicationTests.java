package com.kongtrolink.framework.demo;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.demo.api.FooPublish;
import com.kongtrolink.framework.demo.api.FooPublishImpl;
import com.kongtrolink.framework.entity.MsgResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerDemoApplicationTests {

	@Autowired
	FooPublish fooPublish;

	@Test
	public void sendMsg() {
		String serverCode = com.kongtrolink.framework.common.util.MqttUtils.preconditionServerCode("FOO_SERVER_DEMO_1", "1.0.0");
		for (int i = 0; i < 10; ++i) {
			MsgResult result = fooPublish.sendMsgSyn(serverCode, "foo", "{\"test\": " + i + "}");
			System.out.println(JSONObject.toJSONString(result));
		}
	}

	@Test
	public void contextLoads() {
	}

}
