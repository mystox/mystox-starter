package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.model.ModuleMsg;
import com.kongtrolink.framework.execute.module.model.TerminalMsg;
import com.kongtrolink.framework.runner.ControllerRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MinifsuControllerApplicationTests
{


    @MockBean
    ControllerRunner workerRunner; //测试排除注入服务初始化

    @Test
    public void contextLoads()
    {
    }

    @Autowired
    RpcModule rpcModule;

    /**
     * rpc spring 测试方法
     */
    @Test
    public void sendMsgTest() throws InterruptedException
    {
        InetSocketAddress addr = new InetSocketAddress("172.16.6.39", 18880);
        InetSocketAddress addr2 = new InetSocketAddress("172.16.6.39", 18881);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++)
        {
            System.out.println(i);
            final int a = i;
            Runnable runnable = () ->
            {
                String msgId = UUID.randomUUID().toString();
                RpcNotifyProto.RpcMessage result = null;
                try
                {
//                    int r = new Random().nextInt(10000);
                    System.out.println("创建线程"+a );
//                    Thread.sleep(r);
                    result = rpcModule.postMsg(msgId, addr, "I'm client mystox message...h暗号" + a, 10000l);
                    RpcNotifyProto.RpcMessage result2 = rpcModule.postMsg(msgId, addr2, "I'm client mystox message...h暗号" + a);
                    System.out.println(result.getPayload());
                    System.out.println(result2.getPayload());
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

            };
            threads[i] = new Thread(runnable);
            threads[i].start();
        }
        while (Thread.activeCount() > 8) {
            Thread.sleep(1000);
            System.out.println("活跃线程"+Thread.activeCount());
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
    public void redisTest()
    {
        List<TerminalMsg> a = new ArrayList<>();
        TerminalMsg f = new TerminalMsg();
        TerminalMsg t = new TerminalMsg();


        a.add(f);
        a.add(t);
        redisTemplate.opsForValue().set("a", a);
        String as = "{'a':'b'}";
        redisTemplate.opsForHash().put(RedisHashTable.COMMUNICATION_HASH, "b", JSONObject.parse(as));
        JSONObject r = redisUtils.getHash(RedisHashTable.COMMUNICATION_HASH, "b", JSONObject.class);
        System.out.println(r);
//		List<TerminalMsg> r = JSONArray.
    }

    public static void main(String[] args)
    {
        ModuleMsg moduleMsg = new ModuleMsg();
    }
}
