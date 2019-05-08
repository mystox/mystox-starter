package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.entity.AlarmSignalConfig;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.model.TerminalMsg;
import com.kongtrolink.framework.runner.ControllerRunner;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.ScanParams;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MinifsuControllerApplicationTests {


    @MockBean
    ControllerRunner workerRunner; //测试排除注入服务初始化

    @Test
    public void contextLoads() {
    }

    @Autowired
    RpcModule rpcModule;

    /**
     * rpc spring 测试方法
     */
    @Test
    public void sendMsgTest() throws InterruptedException {
        InetSocketAddress addr = new InetSocketAddress("172.16.6.39", 18880);
        InetSocketAddress addr2 = new InetSocketAddress("172.16.6.39", 18881);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
            final int a = i;
            Runnable runnable = () ->
            {
                String msgId = UUID.randomUUID().toString();
                RpcNotifyProto.RpcMessage result = null;
                try {
//                    int r = new Random().nextInt(10000);
                    System.out.println("创建线程" + a);
//                    Thread.sleep(r);
                    result = rpcModule.postMsg(msgId, addr, "I'm client mystox message...h暗号" + a, 10000l);
                    RpcNotifyProto.RpcMessage result2 = rpcModule.postMsg(msgId, addr2, "I'm client mystox message...h暗号" + a);
                    System.out.println(result.getPayload());
                    System.out.println(result2.getPayload());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            };
            threads[i] = new Thread(runnable);
            threads[i].start();
        }
        while (Thread.activeCount() > 8) {
            Thread.sleep(1000);
            System.out.println("活跃线程" + Thread.activeCount());
//            System.out.println("-----");
            Thread.yield();
        }
        System.out.println("end----------------------------------");
    }


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedisUtils redisUtils;

    @Test
    public void redisTest() {
        List<TerminalMsg> a = new ArrayList<>();
        TerminalMsg f = new TerminalMsg();
        TerminalMsg t = new TerminalMsg();


        a.add(f);
        a.add(t);
        ValueOperations valueOperations = redisTemplate.opsForValue();
//        valueOperations.set("a", a);
        String as = "{'a':'b'}";
//        redisTemplate.opsForHash().put(RedisHashTable.COMMUNICATION_HASH, "b", JSONObject.parse(as));
//        String key = "communication_hash:MINI210121000001";
        String key = "abc";
       /* for (int i = 0; i<1;i++) {
            System.out.println(i);
            JSONObject r = redisUtils.get(key, JSONObject.class);
            redisUtils.set(key, "123",100000);
            System.out.println(r);
        }*/
        ScanParams scanParams = new ScanParams();
        scanParams.match("MINI210121000001*");
        ScanOptions scanOptions = ScanOptions.scanOptions().match("MINI210121000001_3-1_1*").build();

        String pattern = "communication_hash*";
//        final Set<String> hkeys = redisUtils.getHkeys(pattern);
//        Set<String> keys = redisTemplate.keys(pattern);
//        System.out.println(keys);


        /*Set<String> keys = redisUtils.getHkeys(RedisHashTable.SN_DEV_ID_ALARMSIGNAL_HASH, "MINI210121000001" + "*");
        System.out.println(keys);
        if (keys != null && keys.size() > 0) {
            String[] s = new String[keys.size()];
            redisUtils.deleteHash(RedisHashTable.SN_DEV_ID_ALARMSIGNAL_HASH, keys.toArray(s));
        }*/

//        System.out.println(keys.size());
//        redisTemplate.opsForHash().delete(RedisHashTable.SN_DEV_ID_ALARMSIGNAL_HASH, "MINI210121000001*");
//        System.out.println(r);
//		List<TerminalMsg> r = JSONArray.


        JSONArray redisSignalObj = redisUtils.getHash(RedisHashTable.SN_DEV_ID_ALARM_SIGNAL_HASH, "MINI210121000001_6-1_302001", JSONArray.class);
        List<AlarmSignalConfig> alarmSignals = JSONArray.parseArray(redisSignalObj.toString(), AlarmSignalConfig.class);

    }


    @Test
    public void testNet() {
        //1包注册
        String registerMsg = "{\"msgId\":\"000021\",\"pkgSum\":1,\"ts\":101325,\"payload\":{\"pktType\":1,\"SN\":\"MINI210121000001\"}}";
        JSONObject registerNet = new JSONObject();
        String uuid = UUID.randomUUID().toString();
        registerNet.put("uuid", uuid);
        registerNet.put("gip", "172.16.6.39:17700");
        registerNet.put("pktType", PktType.CONNECT);
        registerNet.put("payload", registerMsg);
        JSONObject result = sendPayLoad("", registerNet.toJSONString(), "172.16.6.39", 18800);
        System.out.println("注册结果: " + result);
        if ((Integer) ((Map) result.get("payload")).get("result") == 1) {
            //2包 终端信息
            String terminalMsg = "{\"msgId\":\"000006\",\"pkgSum\":1,\"ts\":1553500102,\"payload\":{\"pktType\":2,\"SN\":\"MINI210121000001\",\"business\":0,\"accessMode\":1,\"carrier\":\"CM\",\"nwType\":\"NB\",\"wmType\":\"A8300\",\"wmVendor\":\"LS\",\"imsi\":\"460042350102767\",\"imei\":\"868348030574374\",\"signalStrength\":24,\"engineVer\":\"1.3.7.2\",\"adapterVer\":\"8.0.0.1\"}}";
            registerNet.put("payload", terminalMsg);
            result = sendPayLoad("", registerNet.toJSONString(), "172.16.6.39", 18800);
            System.out.println("终端信息上传结果: " + result);
            if ((Integer) ((Map) result.get("payload")).get("result") == 1) {

                //3包 设备包
                String deviceMsg = "{\"msgId\":\"000009\",\"pkgSum\":1,\"ts\":1553500113,\"payload\":{\"pktType\":3,\"SN\":\"MINI210121000001\",\"devList\": [\"3-0-0-1-0110103\",\"1-0-1-1-0990101\",\"6-1-1-1-0990201\"]}}";
                registerNet.put("payload", deviceMsg);
                result = sendPayLoad("", registerNet.toJSONString(), "172.16.6.39", 18800);
                System.out.println("设备信息上传结果: " + result);
//                if ((Integer) ((Map) result.get("payload")).get("result") == 1) {
//                    //4包 数据包
//                    String dataMsg = "{\"msgId\":\"000049\",\"pkgSum\":1,\"ts\":1553500171,\"payload\":{\"pktType\":4,\"SN\":\"MINI210121000001\",\"dts\":1553500148,\"data\":[{\"dev\":\"3-1\",\"info\":{\"1001\":5,\"3001\":5,\"301001\":2300,\"302001\":100}}]}}\n";
//                    registerNet.put("payload", dataMsg);
//        result = sendPayLoad("", registerNet.toJSONString(), "172.16.6.39", 18800);
//        System.out.println("数据包信息上传结果: " + result);
//
//                }
            }
        }


    }

    @Test
    public void dataSend() {
        JSONObject registerNet = new JSONObject();
        String uuid = "e93b019a-edc1-4769-a61d-915297bca091";
        registerNet.put("uuid", uuid);
        registerNet.put("gip", "172.16.6.211:17700");
        registerNet.put("pktType", PktType.CONNECT);
        //4包 数据包
//    String dataMsg = "{\"msgId\":\"000049\",\"pkgSum\":1,\"ts\":1553500171,\"payload\":{\"pktType\":4,\"SN\":\"MINI210121000001\",\"dts\":1553500148,\"data\":[{\"dev\":\"3-1\",\"info\":{\"1001\":5,\"3001\":5,\"301001\":2300,\"302001\":100}}]}}\n";
        String dataMsg = "{\"msgId\":\"000049\",\"pkgSum\":1,\"ts\":1553500171,\"payload\":{\"pktType\":4,\"SN\":\"LIUDD210121000001\",\"dts\":1553500148,\"data\":[{\"dev\":\"3-1\",\"info\":{\"1001\":2000000}}]}}\n";
//        String dataMsg = "{\"msgId\":\"000049\",\"pkgSum\":1,\"ts\":1553500171,\"payload\":{\"pktType\":4,\"SN\":\"LIUDD210121000001\",\"dts\":1553500148,\"data\":[{\"dev\":\"3-1\",\"info\":{\"3001\":11}}]}}\n";
        registerNet.put("payload", dataMsg);
        JSONObject result = sendPayLoad("", registerNet.toJSONString(), "172.16.6.211", 18800);
        System.out.println("数据包信息上传结果: " + result);
    }

    @Test
    public void cleanUp() {

        //1包注册
        String registerMsg = "{\"code\":4,\"serverHost\":\"127.0.0.1\",\"serverName\":\"net-GW\",\"time\":1553500102000,\"SN\":\"MINI210121000001\"}";
        JSONObject registerNet = new JSONObject();
        String uuid = UUID.randomUUID().toString();
        uuid = "98485229-8b8d-40f5-a3a5-50ee876ba450";
        registerNet.put("uuid", uuid);
        registerNet.put("gip", "172.16.6.39:17700");
        registerNet.put("pktType", PktType.CLEANUP);
        registerNet.put("payload", JSONObject.parse(registerMsg));
        JSONObject result = sendPayLoad("", registerNet.toJSONString(), "172.16.6.39", 18800);
        System.out.println("注销结果: " + result);
    }

    @Test
    public void registerFSU() {
        String host = "172.16.6.211";
        String registerMsg = "{\"msgId\":\"000021\",\"payload\":{\"pktType\":1,\"SN\":\"LIUDD210121000001\"}}";
        JSONObject registerNet = new JSONObject();
        registerNet.put("uuid", "e93b019a-edc1-4769-a61d-915297bca091");
        registerNet.put("gip", host + ":17700");
        registerNet.put("pktType", PktType.CONNECT);
        registerNet.put("payload", registerMsg);
        JSONObject result = sendPayLoad("", registerNet.toJSONString(), host, 18800);
        System.out.println("注册结果: " + result);
        if ((Integer) ((Map) result.get("payload")).get("result") == 1) {
            //2包 终端信息
            String terminalMsg = "{\"msgId\":\"000006\",\"payload\":{\"pktType\":2,\"SN\":\"LIUDD210121000001\",\"business\":0,\"acessMode\":1,\"carrier\":\"CM\",\"nwType\":\"NB\",\"wmType\":\"A8300\",\"wmVendor\":\"LS\",\"imsi\":\"460042350102767\",\"imei\":\"868348030574374\",\"signalStrength\":24,\"engineVer\":\"1.3.7.2\",\"adapterVer\":\"8.0.0.1\"}}";
            registerNet.put("payload", terminalMsg);
            result = sendPayLoad("", registerNet.toJSONString(), host, 18800);
            System.out.println("终端信息上传结果: " + result);
            if ((Integer) ((Map) result.get("payload")).get("result") == 1) {
                //3包 设备包
                String deviceMsg = "{\"msgId\":\"000009\",\"payload\":{\"pktType\":3,\"SN\":\"LIUDD210121000001\",\"devList\": [\"3-0-0-1-0110103\",\"1-0-1-1-0990101\",\"6-1-1-1-0990201\"]}}";
                registerNet.put("payload", deviceMsg);
                result = sendPayLoad("", registerNet.toJSONString(), host, 18800);
                System.out.println("设备信息上传结果: " + result);
            }
        }
    }


    /**
     * @param msgId
     * @param payload
     * @return
     */
    private JSONObject sendPayLoad(String msgId, String payload, String host, int port) {
        JSONObject result = new JSONObject();
        RpcNotifyProto.RpcMessage response = null;
        try {
            response = rpcModule.postMsg(msgId, new InetSocketAddress(host, port), payload);
            if (RpcNotifyProto.MessageType.ERROR.equals(response.getType()))//错误请求信息
            {
                result.put("result", 0);
            } else {
                return JSONObject.parseObject(response.getPayload());
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.put("result", 0);
        }
        return result;
    }

    public static void main(String[] args) throws InterruptedException {

        //初始化客户端
        Configuration conf = new Configuration();
        RpcClient rpcClient = new RpcClient(conf);
        RpcModuleBase rpcModuleBase = new RpcModuleBase(rpcClient);
        RpcNotifyProto.RpcMessage response = null;

        //1包注册
        JSONObject requestHead = new JSONObject();
        String uuid = UUID.randomUUID().toString(); //uuid只有重新注册才会变更
        requestHead.put("uuid", uuid);
        requestHead.put("gip", "172.16.6.50:17701");
        requestHead.put("pktType", PktType.CONNECT);

        /************************* 注册********************************/
        for (int i = 0; i < 1; i++) {
            String msgId = "00000" + i + "";
            String registerMsg = "{\"msgId\":\"" + msgId + "\",\"payload\":{\"pktType\":1,\"SN\":\"MINI210121000001\"}}";
            response = sendMSG(requestHead, rpcModuleBase, registerMsg);
            System.out.println("终端注册结果: " + response.getPayload());


            if ((Integer) ((Map) JSONObject.parseObject(response.getPayload()).get("payload")).get("result") == 1) { //判断结果是不是1 成功的请求
                //2包 终端信息
                String terminalMsg = "{\"msgId\":\"000006\",\"payload\":{\"pktType\":2,\"SN\":\"MINI210121000001\",\"business\":0,\"acessMode\":1,\"carrier\":\"CM\",\"nwType\":\"NB\",\"wmType\":\"A8300\",\"wmVendor\":\"LS\",\"imsi\":\"460042350102767\",\"imei\":\"868348030574374\",\"signalStrength\":24,\"engineVer\":\"1.3.7.2\",\"adapterVer\":\"8.0.0.1\"}}";
                response = sendMSG(requestHead, rpcModuleBase, terminalMsg);
                System.out.println("终端属性上报结果: " + response.getPayload());
                if ((Integer) ((Map) JSONObject.parseObject(response.getPayload()).get("payload")).get("result") == 1) { //判断结果是不是1 成功的请求
                    //3包 设备包
                    String deviceMsg = "{\"msgId\":\"000009\",\"payload\":{\"pktType\":3,\"SN\":\"MINI210121000001\",\"devList\": [\"1-1-1-1-0990201\"]}}";
                    response = sendMSG(requestHead, rpcModuleBase, deviceMsg);
                    System.out.println("设备上报结果" + response.getPayload());
                }

            }
            //3包 数据包 数据变化包
            String dataMsg = "{\"msgId\":\"000049\",\"pkgSum\":1,\"ts\":1553500171,\"payload\":{\"pktType\":4,\"SN\":\"MINI210121000001\",\"dts\":1553500148,\"data\":[{\"dev\":\"3-1\",\"info\":{\"1001\":12}},{\"dev\":\"3-1\",\"info\":{\"201001\":12}},{\"dev\":\"3-1\",\"info\":{\"101001\":12,\"101002\":12}},{\"dev\":\"3-1\",\"info\":{\"301001\":12}}]}}\n";
            requestHead.put("payload", dataMsg);
            response = sendMSG(requestHead, rpcModuleBase, dataMsg);
            System.out.println("设备上报结果" + response.getPayload());
            //11包 运状包
            Thread.sleep(5000L);
            String data = "{\"msgId\":\"000044\",\"pkgSum\":1,\"ts\":1553500171,\"payload\":{\n" +
                    "  \"pktType\": 11,\n" +
                    "  \"SN\": \"MINI210121000001\",\n" +
                    "  \"cpuUse\":\"37.2%\",\n" +
                    "  \"memUse\":\"41.5%\",\n" +
                    "  \"sysTime\":15234875,\n" +
                    "  \"csq\":22\n" +
                    "}}\n";
            requestHead.put("payload", data);
            response = sendMSG(requestHead, rpcModuleBase, data);
            System.out.println("设备上报结果" + response.getPayload());
            Thread.sleep(5000L);
            String heart = "{\"msgId\":\"000050\",\"ts\":1553500171,\"payload\":{\"pktType\":0,\"SN\":\"MINI210121000001\"}}\n";
            requestHead.put("payload", heart);
            response = sendMSG(requestHead, rpcModuleBase, heart);
            System.out.println("设备心跳结果" + response.getPayload());
        }
        /*String cleanMsg = "{\"code\":4,\"serverHost\":\"127.0.0.1\",\"serverName\":\"net-GW\",\"time\":1553500102000}";
        System.out.println(cleanMsg);
        requestHead.put("pktType", PktType.CLEANUP);
        response = sendMSG(requestHead, rpcModuleBase,  JSONObject.parse(cleanMsg));
        System.out.println("注销"+response.getPayload());*/


        /****************** 文件包***************************//*
        String msgId = "00000"+ 1 + "";
        String fileMsg = "{\"msgId\":\""+msgId+"\",\"payload\":{\n" +
                "  \"pktType\": 10,\n" +
                "  \"SN\": \"MINI210121000001\",\n" +
                "  \"file\": {\n" +
                "    \"filename\": \"engine.bin\",\n" +
                "    \"type\" : 1,\n" +
                "    \"fileNum\" : 2,\n" +
                "    \"startIndex\": 2,\n" +
                "    \"endIndex\": 5\n" +
                "  }\n" +
                "}}";
        requestHead.put("payload", fileMsg);
        response = sendMSG(requestHead, rpcModuleBase, fileMsg);
        System.out.println("文件流结果: "+response.getBytePayload().toString());*/

    }


    static RpcNotifyProto.RpcMessage sendMSG(JSONObject requestHead, RpcModuleBase rpcModuleBase, Object msg) {
        String ip = "172.16.6.50";
        int port = 18800;
        RpcNotifyProto.RpcMessage response = null;
        requestHead.put("payload", msg);
        try {
            response = rpcModuleBase.postMsg("", new InetSocketAddress(ip, port), requestHead.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


}
