package tech.mystox.framework;

import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import tech.mystox.demo.SpringbootYydsMqttDemoApplication;
import tech.mystox.framework.api.test.EntityService;
import tech.mystox.framework.api.test.entity.OperaParam;
import tech.mystox.framework.stereotype.Opera;

//
//import com.alibaba.fastjson2.JSONObject;
//import org.apache.commons.io.FileUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.core.env.Environment;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.yaml.snakeyaml.DumperOptions;
//import org.yaml.snakeyaml.Yaml;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by mystoxlol on 2019/8/26, 14:16.
// * description:
// * update record:
// */
//@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootYydsMqttDemoApplication.class)
public class SpringbootYydsMqttDemoApplicationTest {

    @Opera
    EntityService entityService;

    @Test
    public void testT() {
        JSONObject j = new JSONObject();
        j.put("dsfasdaf", "dsfdf");
        OperaParam operaParam = new OperaParam();
        operaParam.setParam("123A");
        operaParam.setContext("123123");
        //        OperaParam t = entityService.getT(operaParam);
        //        System.out.println(t);

    }

    @Value("${a:#{47 + 1024}}")
    private Integer mqttPayloadLimit;

    @Test
    public void testEntity() {
        String hello = entityService.hello();
        System.out.println(hello);
    }

    @Test
    public void testValue() {
        System.out.println(mqttPayloadLimit);
    }




    //    @MockBean
    //    private MqttConfig mqttConfig;
    //    @MockBean
    //    private MqttSender mqttSender;
    //    @MockBean
    //    MqttReceiverImpl mqttReceiverImpl;
    //
    //    @MockBean
    //    RegisterRunner registerRunner;
    //
    //
    //    @Test
    //    public void testYaml() {
    //        DumperOptions dumperOptions = new DumperOptions();
    //        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    //        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
    //        dumperOptions.setPrettyFlow(false);
    //        Yaml yaml = new Yaml(dumperOptions);
    //
    //        try {
    //            File file = FileUtils.getFile("E:\\IdeaProjects\\YYDS\\configResources\\gateway-transverterRelation.yml");
    //            System.out.println(file.getAbsolutePath());
    //            TransverterConfig load = yaml.loadAs(new FileInputStream(file),TransverterConfig.class);
    //
    //            Map<String, List<String>> values = new HashMap<>();
    //
    //            List<String> valueList = new ArrayList<>();
    //            valueList.add("abc");
    //            valueList.add("123");
    //            values.put("alarm", valueList);
    ////            load.put("transverter", values);
    //            System.out.println(JSONObject.toJSONString(load));
    //
    //            yaml.dump(load,new FileWriter(file));
    //
    //        } catch (FileNotFoundException e) {
    //            e.printStackTrace();
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    @Autowired
    //    Environment environment;
    //
    //    @Test
    //    public void TestScan() {
    //    }
    //
    //
    //
    //
}
