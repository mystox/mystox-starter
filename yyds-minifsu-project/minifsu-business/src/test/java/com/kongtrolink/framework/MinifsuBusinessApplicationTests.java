package com.kongtrolink.framework;

import com.kongtrolink.framework.core.entity.MongoTableName;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.model.Order;
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
import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MinifsuBusinessApplicationTests {



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

		Order aDefault = mongoTemplate.findById("default", Order.class, MongoTableName.ORDER);
		System.out.println(aDefault.getBID());
//		List<RunState> r = mongoTemplate.find(Query.query(Criteria.where("createTime").lte(new Date(0))), RunState.class, MongoTableName.TERMINAL_RUN_STATE);
//		System.out.println(r.size());
//		SignalModel one = mongoTemplate.findOne(Query.query(Criteria.where("deviceType").is(1).and("dataId").is("1001")), SignalModel.class, MongoTableName.SIGNAL_MODEL);
//		List<SignalModel> all = mongoTemplate.findAll(SignalModel.class, MongoTableName.SIGNAL_MODEL);
//		System.out.println(all.size());
		Criteria criteria = Criteria.where("sn").is("MINI201904260012");
		Long startTime = 1556121600000L;
		if (startTime != null) {
			Long endTime = 1556553600000L;
			if (endTime != null) {
				criteria.and("createTime").gte(new Date(startTime)).lte(new Date(endTime));
			} else {
				criteria.and("createTime").gte(new Date(startTime));
			}
		}
//		mongoTemplate.count(Query.query())
	}
}