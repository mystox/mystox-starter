package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.TransverterConfig;
import com.kongtrolink.framework.mqtt.config.MqttConfig;
import com.kongtrolink.framework.mqtt.service.impl.MqttReceiverImpl;
import com.kongtrolink.framework.register.runner.RegisterRunner;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/8/26, 14:16.
 * company: kongtrolink
 * description:
 * update record:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootYydsMqttDemoApplicationTest {

    @MockBean
    private MqttConfig mqttConfig;
    @MockBean
    private MqttSender mqttSender;

    @MockBean
    MqttReceiverImpl mqttReceiverImpl;


    @MockBean
    RegisterRunner registerRunner;


    @Test
    public void testYaml() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);

        try {
            File file = FileUtils.getFile("E:\\IdeaProjects\\YYDS\\configResources\\gateway-transverterRelation.yml");
            System.out.println(file.getAbsolutePath());
            TransverterConfig load = yaml.loadAs(new FileInputStream(file),TransverterConfig.class);

            Map<String, List<String>> values = new HashMap<>();

            List<String> valueList = new ArrayList<>();
            valueList.add("abc");
            valueList.add("123");
            values.put("alarm", valueList);
//            load.put("transverter", values);
            System.out.println(JSONObject.toJSONString(load));

            yaml.dump(load,new FileWriter(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    Environment environment;

    @Test
    public void TestScan() {
    }




}
