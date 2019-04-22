package com.kongtrolink.framework;

import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.runner.CntbGWRunner;
import com.kongtrolink.framework.util.FSUServiceClientUtil;
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
public class MinifsuCntbGWApplicationTests {

	@MockBean
    CntbGWRunner alarmRunner; //测试排除注入服务初始化
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
		String xml = "<Request><PK_Type><Name>LOGIN</Name><Code>101</Code></PK_Type><Info><UserName>ttcw2015</UserName><PaSCword>ttcw@2015</PaSCword><FsuId>13012943800224</FsuId><FsuCode>13012943800224</FsuCode><FsuIP>10.133.79.53</FsuIP><MacId>869221027913526</MacId><ImsiId>460012321309411</ImsiId><NetworkType>3G</NetworkType><Carrier>CU</Carrier><NMVendor>大唐</NMVendor><NMType>DTM8909</NMType><Reg_Mode>2</Reg_Mode><FSUVendor>TDYY</FSUVendor><Version>1.3.19.0</Version><DictVersion>1</DictVersion><DeviceList /></Info></Request>";
		String response = FSUServiceClientUtil.sendReq(xml, "10.58.128.16", 8080);
		System.out.println(response);
	}
}
