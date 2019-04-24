package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.entity.RedisTable;
import com.kongtrolink.framework.execute.module.dao.AlarmDao;
import com.kongtrolink.framework.execute.module.dao.SignalDao;
import com.kongtrolink.framework.execute.module.model.Alarm;
import com.kongtrolink.framework.execute.module.model.Signal;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.entity.xml.msg.SetPoint;
import com.kongtrolink.framework.entity.xml.msg.GetData;
import com.kongtrolink.framework.entity.xml.util.MessageUtil;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.runner.TowerRunner;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

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
	public void testAnalyzeSetPoint() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><PK_Type><Name>SET_POINT</Name><Code>1001</Code></PK_Type><Info><FsuId>43021143800008</FsuId><FsuCode>43021143800008</FsuCode><Value><DeviceList><Device Id=\"43021149900251\" Code=\"43021149900251\">" +
				"<TSemaphore Type=\"4\" Id=\"0499201001\" MeasuredVal=\"\" SetupVal=\"1\" Status=\"0\"/>" +
				"</Device>" +
				"</DeviceList></Value></Info></Request>";
		Document doc = null;
		SetPoint result;
		try {
			doc = MessageUtil.stringToDocument(xml);
			Node reqInfoNode = doc.getElementsByTagName("Info").item(0);
			String reqInfoStr = MessageUtil.infoNodeToString(reqInfoNode);
			result = (SetPoint)MessageUtil.stringToMessage(reqInfoStr, SetPoint.class);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAnalyzeGetData() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><PK_Type><Name>GET_DATA</Name><Code>401</Code></PK_Type><Info><FsuId>43021143800008</FsuId><FsuCode>43021143800008</FsuCode><DeviceList><Device Id=\"99999999999999\" Code=\"99999999999999\"><Id>9999999999</Id></Device></DeviceList></Info></Request>";
		Document doc = null;
		GetData result;
		try {
			doc = MessageUtil.stringToDocument(xml);
			Node reqInfoNode = doc.getElementsByTagName("Info").item(0);
			String reqInfoStr = MessageUtil.infoNodeToString(reqInfoNode);
			result = (GetData)MessageUtil.stringToMessage(reqInfoStr, GetData.class);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Autowired
	SignalDao signalDao;

	@Autowired
	AlarmDao alarmDao;

	@Test
	public void testRedis() {
//		RedisData redisData = redisUtils.get(RedisTable.DATA_HASH + "1:1", RedisData.class);

//		System.out.println(redisUtils.hHasKey("12", "23"));
//		redisUtils.hset("12", "24", "re");
//		System.out.println(redisUtils.hHasKey("12", "23"));

		Set<String> list = redisUtils.keys(RedisTable.DATA_HASH + "43048243800189:" + "*");
		System.out.println(list);
	}

	@Test
	public void testReadFile() {
		Map<Integer, String> types = new HashMap();
		types.put(0, "19");
		types.put(1, "06");
		types.put(2, "16");
		types.put(3, "15");
		types.put(4, "08");
		types.put(5, "47");
		types.put(6, "45");
		types.put(7, "17");
		types.put(8, "37");
		types.put(9, "05");
		types.put(10, "25");
		types.put(11, "07");
		types.put(12, "46");
		types.put(13, "20");
		types.put(14, "36");
		types.put(100, "184");
		types.put(101, "182");
		types.put(102, "181");
		types.put(140, "183");
		types.put(141, "186");
		types.put(143, "07");
		types.put(180, "99");
		types.put(200, "184");
		types.put(201, "182");
		types.put(202, "181");
		types.put(203, "185");
		types.put(204, "188");
		types.put(205, "183");
		types.put(206, "186");
		types.put(207, "07");
		types.put(208, "44");
		types.put(209, "99");

		for (int type : types.keySet()) {
			String cntbType = types.get(type);
			String signalPath = "C:\\Users\\Administrator\\Desktop\\cntb\\signal" + cntbType + ".csv";
			String alarmPath = "C:\\Users\\Administrator\\Desktop\\cntb\\alarm_signal" + cntbType + ".csv";

			String pathname = "input.txt"; // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
			//防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
			//不关闭文件会导致资源的泄露，读写文件都同理
			//Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271

			try (FileReader reader = new FileReader(signalPath);
				 BufferedReader br = new BufferedReader(reader)) {
				String line;
				//网友推荐更加简洁的写法
				while ((line = br.readLine()) != null) {
					// 一次读入一行数据
					System.out.println(line);
					String[] info = line.split(",");
					if (info.length > 8) {
						Signal signal = new Signal();
						signal.setType(type);
						signal.setSignalId(info[1].substring(3, 9));
						signal.setCntbId("0" + info[1]);
						signal.setIdType(Integer.parseInt(info[2]));
						signal.setName(info[3]);
						signal.setSimpleEnable(info[7].equals("1"));
						if (info.length == 10) {
							signal.setUnit(info[9]);
						} else {
							signal.setUnit("");
						}
						signalDao.upsert(signal);
						System.out.println(JSONObject.toJSONString(signal));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try (FileReader reader = new FileReader(alarmPath);
				 BufferedReader br = new BufferedReader(reader)) {
				String line;
				//网友推荐更加简洁的写法
				while ((line = br.readLine()) != null) {
					// 一次读入一行数据
					System.out.println(line);
					String[] info = line.split(",");
					if (info.length == 17) {
						Alarm alarm = new Alarm();
						alarm.setType(type);
						alarm.setAlarmId(info[1].substring(3, 9));
						alarm.setCntbId("0" + info[1]);
						alarm.setLevel(Integer.parseInt(info[7]));
						alarm.setDesc(info[15]);
						alarmDao.upsert(alarm);
						System.out.println(JSONObject.toJSONString(alarm));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		//初始化客户端
		Configuration conf = new Configuration();
		RpcClient rpcClient = new RpcClient(conf);
		RpcModuleBase rpcModuleBase = new RpcModuleBase(rpcClient);
		RpcNotifyProto.RpcMessage response = null;

//		ModuleMsg msg = createBindRequest();

//		ModuleMsg msg = createRegistryCntbRequest();

//		ModuleMsg msg = createTimeCheckRequest();

//		ModuleMsg msg = createDataChangeRequest();

		ModuleMsg msg = createAlarmRequest();

		response = sendMSG(rpcModuleBase, msg);
		System.out.println("终端属性上报结果: "+response.getPayload());
	}

	private static ModuleMsg createBindRequest() {
		ModuleMsg msg = new ModuleMsg(PktType.FSU_BIND, "MINI210121000001");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("fsuId", "43048243800189");
		jsonObject.put("sn", "MINI210121000001");
		jsonObject.put("name", "minifsu");
		jsonObject.put("address", "浙江杭州");
		jsonObject.put("desc", "测试站点");
		jsonObject.put("vpnName", "全国3");
		jsonObject.put("fsuClass", "INTSTAN");

		List<String> list = new ArrayList();
		list.add("43048243800189");
		list.add("43048240700215");
		list.add("43048240600130");
		list.add("43048241820217");
		list.add("43048243700210");
		list.add("43048241800169");
		list.add("43048241830216");
		list.add("43048241840216");
		list.add("43048241810169");
		list.add("43048241500109");
		list.add("43048241900261");
		list.add("43048249900010");
		list.add("43048244500016");
		list.add("43048241860244");
		jsonObject.put("devCodeList", list);

		msg.setPayload(jsonObject);

		return msg;
	}

	private static ModuleMsg createRegistryCntbRequest() {
		ModuleMsg msg = new ModuleMsg(PktType.REGISTRY_CNTB, "MINI210121000001");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("SN", "MINI210121000001");
		jsonObject.put("innerIp", "172.16.6.20");
		jsonObject.put("innerPort", 18883);

		List<String> list = new ArrayList();
		list.add("0-255-0-0-0110103"); //监控设备
		list.add("1-1-1-1-0990101"); //开关电源
		list.add("3-2-1-1-0990201"); //普通空调
		list.add("102-0-1-1-0990201"); //红外
		list.add("101-0-1-1-0990201"); //烟感
		list.add("140-0-1-1-0990201"); //温湿度
		list.add("100-0-1-1-0990201"); //水浸
		list.add("11-0-1-1-0990201"); //蓄电池
		jsonObject.put("devList", list);

		msg.setPayload(jsonObject);

		return msg;
	}

	private static ModuleMsg createTimeCheckRequest() {
		ModuleMsg msg = new ModuleMsg(CntbPktTypeTable.TIME_CHECK);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msg", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><PK_Type><Name>TIME_CHECK</Name><Code>1301</Code></PK_Type><Info><Time><Years>1</Years><Month>2</Month><Day>3</Day><Hour>4</Hour><Minute>5</Minute><Second>6</Second></Time></Info></Request>");

		msg.setPayload(jsonObject);

		return msg;
	}

	private static ModuleMsg createDataChangeRequest() {
		ModuleMsg msg = new ModuleMsg(PktType.DATA_CHANGE, "MINI210121000001");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("SN", "MINI210121000001");

		List<JSONObject> list = new ArrayList<>();

		JSONObject data1 = new JSONObject();
		data1.put("dev", "1-1");
		JSONObject signal1 = new JSONObject();
		signal1.put("101001", 227.03);
		signal1.put("111001", 53.49);
		signal1.put("112001", 29.90);
		signal1.put("113001", 15.33);
		signal1.put("113002", 14.05);
		signal1.put("127001", 150.00);
		data1.put("info", signal1);

		JSONObject data2 = new JSONObject();
		data2.put("dev", "11-1");
		JSONObject signal2 = new JSONObject();
		signal2.put("001001", 0);
		signal2.put("002001", 0);
		signal2.put("005001", 0);
		signal2.put("102001", 53.35);
		signal2.put("106001", 26.72);
		signal2.put("107001", 26.63);
		data2.put("info", signal2);

		list.add(data1);
		list.add(data2);
		jsonObject.put("data", list);

		msg.setPayload(jsonObject);

		return msg;
	}

	private static ModuleMsg createAlarmRequest() {
		ModuleMsg msg = new ModuleMsg(PktType.ALARM_REGISTER, "MINI210121000001");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sN", "MINI210121000001");

		List<JSONObject> list = new ArrayList<>();

		JSONObject alarm1 = new JSONObject();
		alarm1.put("recoverDelay", 0);
		alarm1.put("delay", 0);
		alarm1.put("beginDelayFT", 0);
		alarm1.put("dev_colId", "3-1_3001");
		alarm1.put("num", 7);
		alarm1.put("h", 0);
		alarm1.put("link", 17);
		alarm1.put("alarmId", "001001");
		alarm1.put("tReport", 1555898299123l);
		alarm1.put("tRecover", 1555898299);
		alarm1.put("value", 333.0);
		alarm1.put("recoverDelayFT", 0);

		list.add(alarm1);

		JSONObject alarm2 = new JSONObject();
		alarm2.put("recoverDelay", 0);
		alarm2.put("delay", 0);
		alarm2.put("beginDelayFT", 0);
		alarm2.put("dev_colId", "3-1_3001");
		alarm2.put("num", 8);
		alarm2.put("h", 0);
		alarm2.put("link", 17);
		alarm2.put("alarmId", "003001");
		alarm2.put("tReport", 1555898299);
		alarm2.put("tRecover", 1555898299);
		alarm2.put("value", 333.0);
		alarm2.put("recoverDelayFT", 0);

		list.add(alarm2);

		jsonObject.put("alarmList", list);
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
