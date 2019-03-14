package com.kongtrolink.framework;

import com.cass.protorpc.ProtocolEnum;
import com.cass.protorpc.RpcNotify;
import com.cass.rpc.protorpc.RpcNotifyProto;
import com.kongtrolink.framework.connector.RpcClientModule;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.RPC;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetSocketAddress;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinifsuClientApplication.class)

public class MinifsuClientApplicationTests {

	@Test
	public void contextLoads() {
	}


	@Autowired
	RpcClientModule rpcModule;

	/**
	 * rpc spring 测试方法
	 */
	@Test
	public void sendMsgTest()
	{
		InetSocketAddress addr = new InetSocketAddress("172.16.6.39",18888);
		try
		{
			RpcNotifyProto.RpcMessage result = (RpcNotifyProto.RpcMessage) rpcModule.postMsg(addr,"I'm client message...");
			System.out.println(result.getContent());
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * rpcmain测试方法
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		Configuration conf = new Configuration();
		String ADDRESS = "172.16.6.142";
		int port = 9981;

		ADDRESS = "172.16.6.39";
		port = 9998;
		port = 18888;
		RpcNotifyProto.RpcMessage message = RpcNotifyProto.RpcMessage.newBuilder().setContent("hello i'm clinet111").build();
		byte[] bytes = message.toByteArray();
		System.out.println(bytes.length);
		RPC.setProtocolEngine(conf, RpcNotify.class, ProtobufRpcEngine.class);
		RpcNotify proxy = RPC.getProxy(RpcNotify.class, ProtocolEnum.VERSION,  new InetSocketAddress(ADDRESS,port),
				conf);
		RpcNotifyProto.RpcMessage  result = proxy.notify(null,message);
//        for (int i=0;i<10; i++)
//            result = proxy.Notify(null,message);
		System.out.println("client result:" + result);
	}

}
