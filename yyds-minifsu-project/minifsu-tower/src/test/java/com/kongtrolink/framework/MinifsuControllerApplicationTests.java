package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
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

	public static void main(String[] args) {

		//初始化客户端
		Configuration conf = new Configuration();
		RpcClient rpcClient = new RpcClient(conf);
		RpcModuleBase rpcModuleBase = new RpcModuleBase(rpcClient);
		RpcNotifyProto.RpcMessage response = null;

		ModuleMsg msg = new ModuleMsg("fsu_bind", "PMU20000001");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fsuId", "00000043800001");
		jsonObject.put("sn", "PMU20000001");
		jsonObject.put("name", "新基站");
		jsonObject.put("address", "浙江杭州");
		jsonObject.put("desc", "测试站点1");
		jsonObject.put("vpnName", "全国1");

		List<String> list = new ArrayList();
		list.add("00000040600001");
		list.add("00000041900001");
		list.add("00000040700002");
		list.add("00000043800001");
		list.add("00000040700001");
		jsonObject.put("deviceList", list);

		msg.setPayload(jsonObject);

		response = sendMSG(rpcModuleBase, msg);
		System.out.println("终端属性上报结果: "+response.getPayload());
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
