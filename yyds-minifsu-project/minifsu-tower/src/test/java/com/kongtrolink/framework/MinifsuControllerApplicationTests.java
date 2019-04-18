package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.runner.TowerRunner;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MinifsuControllerApplicationTests {



	@MockBean
    TowerRunner towerRunner; //测试排除注入服务初始化
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
	RedisUtils redisUtils;
	@Test
	public void redisTest()
	{
		redisUtils.keys("*");
	}

	public static void main(String[] args) {

		//初始化客户端
		Configuration conf = new Configuration();
		RpcClient rpcClient = new RpcClient(conf);
		RpcModuleBase rpcModuleBase = new RpcModuleBase(rpcClient);
		RpcNotifyProto.RpcMessage response = null;

//		ModuleMsg msg = createBindRequest();

		ModuleMsg msg = createRegistryCntbRequest();

		response = sendMSG(rpcModuleBase, msg);
		System.out.println("终端属性上报结果: "+response.getPayload());
	}

	private static ModuleMsg createBindRequest() {
		ModuleMsg msg = new ModuleMsg(PktType.FSU_BIND, "PMU20000003");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fsuId", "00000043800002");
		jsonObject.put("sn", "PMU20000003");
		jsonObject.put("name", "新基站");
		jsonObject.put("address", "浙江杭州");
		jsonObject.put("desc", "测试站点3");
		jsonObject.put("vpnName", "全国3");
		jsonObject.put("fsuClass", "INTSTAN");

		List<String> list = new ArrayList();
		list.add("00000040600002");
		list.add("00000041900002");
		list.add("00000040700004");
		list.add("00000043800002");
		list.add("00000041810001");
		list.add("00000040700002");
		list.add("00000041810002");
		jsonObject.put("deviceList", list);

		msg.setPayload(jsonObject);

		return msg;
	}

	private static ModuleMsg createRegistryCntbRequest() {
		ModuleMsg msg = new ModuleMsg(PktType.REGISTRY_CNTB, "PMU20000003");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("SN", "PMU20000003");
		jsonObject.put("innerIp", "172.16.6.20");
		jsonObject.put("innerPort", 18883);

		List<String> list = new ArrayList();
		list.add("255-0-0-0-0110103");
		list.add("1-0-1-1-0990101");
		list.add("6-1-1-1-0990201");
		jsonObject.put("devList", list);

		msg.setPayload(jsonObject);

		return msg;
	}

	static RpcNotifyProto.RpcMessage sendMSG(RpcModuleBase rpcModuleBase, ModuleMsg msg) {
		String ip = "172.16.6.20";
		int port = 18881;
		RpcNotifyProto.RpcMessage response = null;
		try {
			response = rpcModuleBase.postMsg("", new InetSocketAddress(ip, port), JSONObject.toJSONString(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}


}
