package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.model.TerminalMsg;
import com.kongtrolink.framework.runner.ControllerRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        valueOperations.set("a", a);
        String as = "{'a':'b'}";
        redisTemplate.opsForHash().put(RedisHashTable.COMMUNICATION_HASH, "b", JSONObject.parse(as));
        JSONObject r = redisUtils.getHash(RedisHashTable.COMMUNICATION_HASH, "b", JSONObject.class);
        System.out.println(r);
//		List<TerminalMsg> r = JSONArray.
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
                if ((Integer) ((Map) result.get("payload")).get("result") == 1) {
                    //4包 数据包
                    String dataMsg = "{\"msgId\":\"000049\",\"pkgSum\":1,\"ts\":1553500171,\"payload\":{\"pktType\":4,\"SN\":\"MINI210121000001\",\"dts\":1553500148,\"data\":[{\"dev\":\"3-1\",\"info\":{\"1001\":5,\"3001\":5,\"301001\":2300,\"302001\":100}}]}}\n";
                    registerNet.put("payload", dataMsg);
        result = sendPayLoad("", registerNet.toJSONString(), "172.16.6.39", 18800);
        System.out.println("数据包信息上传结果: " + result);

                }
            }
        }


    }


    @Test
    public void cleanUp() {

        //1包注册
        String registerMsg = "{\"code\":4,\"serverHost\":\"127.0.0.1\",\"serverName\":\"net-GW\",\"time\":1553500102000,\"SN\":\"MINI210121000001\"}";
        JSONObject registerNet = new JSONObject();
        String uuid = UUID.randomUUID().toString();
        registerNet.put("uuid", uuid);
        registerNet.put("gip", "172.16.6.39:17700");
        registerNet.put("pktType", PktType.CLEANUP);
        registerNet.put("payload", JSONObject.parse(registerMsg));
        JSONObject result = sendPayLoad("", registerNet.toJSONString(), "172.16.6.39", 18800);
        System.out.println("注销结果: " + result);
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

    public static void main(String[] args) {
        String s = "{\"msgId\":\"000009\",\"pkgSum\":1,\"ts\":1553500113,\"payload\":{\"pktType\":3,\"SN\":\"MINI210121000001\",\"devList\": [\"255-0-0-0-0110103\",\"1-0-1-1-0990101\",\"6-1-1-1-0990201\"]}}";

        JSONObject jsonObject = JSONObject.parseObject(s);
        System.out.println(JSONObject.parseObject(s));
//        System.out.println(jsonObject.get("data"));
//        JSONObject object = (JSONObject) jsonObject.get("data");
//        System.out.println(object.keySet());
//
//        System.out.println((Map)jsonObject.get("data"));


    }

    public void testMongo() {

    }

}
