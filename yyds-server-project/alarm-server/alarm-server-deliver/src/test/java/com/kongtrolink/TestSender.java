package com.kongtrolink;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.enttiy.InformMsg;
import com.kongtrolink.framework.service.MqttSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: liudd
 * @Date: 2019/11/26 15:49
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSender {

    @Autowired
    MqttSender mqttSender;

    @Test
    public void test1(){
        String serverVerson = "ALARM_SERVER_SENDER_TEST_V1.0.0";
        String operateCode = "handleSender";
        InformMsg informMsg = new InformMsg();
        informMsg.setType(Contant.TEMPLATE_MSG);
        informMsg.setInformAccount("15267071976");
        informMsg.setTempCode("1144");
        informMsg.setAddressName("测试地址112601");
        informMsg.setAlarmName("测试告警112601");
        mqttSender.sendToMqtt(serverVerson, operateCode, JSONObject.toJSONString(informMsg));
    }
}
