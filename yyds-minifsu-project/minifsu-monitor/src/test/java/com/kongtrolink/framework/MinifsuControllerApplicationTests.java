package com.kongtrolink.framework;

import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.runner.AlarmRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MinifsuControllerApplicationTests {



	@MockBean
    AlarmRunner alarmRunner; //测试排除注入服务初始化
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
		InetSocketAddress addr = new InetSocketAddress("localhost",18880);
		try
		{
			String msgId = UUID.randomUUID().toString();
			RpcNotifyProto.RpcMessage result = rpcModule.postMsg(msgId,addr,"I'm client mystox message...h暗号");
			System.out.println("----"+result.getPayload());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
