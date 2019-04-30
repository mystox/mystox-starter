package com.kongtrolink.gateway.nb.cmcc;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.gateway.nb.cmcc.entity.base.BaseAck;
import com.kongtrolink.gateway.nb.cmcc.entity.iot.ResourceAck;
import com.kongtrolink.gateway.nb.cmcc.entity.iot.ResourceInstances;
import com.kongtrolink.gateway.nb.cmcc.entity.iot.ResourceItem;
import com.kongtrolink.gateway.nb.cmcc.iot.service.NbIotService;
import com.kongtrolink.gateway.nb.cmcc.runner.AlarmRunner;
import com.kongtrolink.gateway.nb.cmcc.util.EntityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by mystoxlol on 2019/4/17, 11:06.
 * company: kongtrolink
 * description:
 * update record:
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootAplicationTest {
    @MockBean
    AlarmRunner workerRunner; //测试排除注入服务初始化
    @Autowired
    NbIotService service;
    @Test
    public void test() throws Exception {
        String imei = "869060030442697";
        BaseAck resources = service.getResources("869060030442697");
        System.out.println(resources);


        ResourceAck ack  = EntityUtil.getEntity(resources,ResourceAck.class);
        ResourceItem resourceItem =ack.getItem().get(0);
        Integer objId = resourceItem.getObj_id();
        ResourceInstances resourceInstances = resourceItem.getInstances().get(0);
        Integer resId = resourceInstances.getResources()[0];
        Integer instId = resourceInstances.getInst_id();
        JSONObject a = new JSONObject();
        a.put("abc","123");
        BaseAck command = service.command(a.toJSONString(), imei, objId, instId, resId);
//        byte[] bytes = new byte[]{0x01,0x00,0x66,0x10,0x12};
//        BaseAck command = service.command(bytes, imei, objId, instId, resId);
        System.out.println(command);






    }
}
