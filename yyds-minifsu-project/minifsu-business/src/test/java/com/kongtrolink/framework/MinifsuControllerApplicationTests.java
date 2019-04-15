package com.kongtrolink.framework;

import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.runner.BusinessRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MinifsuControllerApplicationTests {



	@MockBean
    BusinessRunner alarmRunner; //测试排除注入服务初始化
	@Test
	public void contextLoads() {
	}

	@Autowired
	RpcModule rpcModule;
	/**
	 * rpc spring 测试方法
	 */
	@Test
	public void sendMsgTest()
	{
		InetSocketAddress addr = new InetSocketAddress("localhost",17777);
		try
		{
			String msgId = UUID.randomUUID().toString();
			RpcNotifyProto.RpcMessage result = rpcModule.postMsg(msgId,addr,"I'm client mystox message...h暗号");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Autowired
	MongoTemplate mongoTemplate;
	/**
	 * rpc spring 测试方法
	 */
	@Test
	public void testMongo()
	{
		Criteria criteria =Criteria.where("a").is(1);
		criteria.and("b").is(2);
		System.out.println(criteria);
	}
}
