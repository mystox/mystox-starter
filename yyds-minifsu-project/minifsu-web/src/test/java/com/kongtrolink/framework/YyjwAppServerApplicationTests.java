package com.kongtrolink.framework;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.util.JsonResult;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YyjwAppServerApplicationTests {
    Logger logger = LoggerFactory.getLogger("~");


    @Test
    public void contextLoads() {
       /* logger.info("初始化信号点code... ...");
//        Resource resource = new ClassPathResource("config/signal");
        try {
            File directory = ResourceUtils.getFile("../../AppResources/config1");
            System.out.println(directory.getAbsolutePath());
            Map<String, SignalCode> signalMap = new HashMap<>();
            File[] files = directory.listFiles();
            logger.info("signal files size is " + files.length);
            for (File file : files) {
                if (file.getName().contains("alarm")) {
                    List<String> result = FileUtils.readLines(file, Charset.forName("UTF-8").toString());
                   *//* for (String signalString : result) {
                        if (signalString == null || !signalString.contains(","))
                            continue;
                        String[] signalStrArr = signalString.split(",");
                        SignalCode signalCode = new SignalCode();
                        if (signalStrArr.length > 9) {
                            signalCode.setUnit(signalStrArr[9]);
                        }
                        signalCode.setName(signalStrArr[3]);
                        String signalId = signalStrArr[1];
                        signalMap.put(signalId, signalCode);
                    }*//*
                }
                if (file.getName().startsWith("signal")) {
                    List<String> result = FileUtils.readLines(file, Charset.forName("UTF-8").toString());
                    for (String signalString : result) {
                        if (signalString == null || !signalString.contains(","))
                            continue;
                        String[] signalStrArr = signalString.split(",");

                        *//*SignalCode signalCode = new SignalCode();
                        if (signalStrArr.length > 9) {
                            signalCode.setUnit(signalStrArr[9]);
                        }
                        signalCode.setName(signalStrArr[3]);
                        String signalId = signalStrArr[1];
                        signalMap.put(signalId, signalCode);*//*
                    }
                }
            }
            ControllerInstance.getInstance().setSignalMap(signalMap);
        } catch (IOException e) {
            e.printStackTrace();
        }*/




    }

    @MockBean
    RedisOperationsSessionRepository redisOperationsSessionRepository;


    @Autowired
    RestTemplate restTemplate;

    @Value("${nb.url.cmcc}")
    private String cmccNetUrl;

    @Test
    public void testTemplate() {
        JSONObject deviceJson = new JSONObject();
        deviceJson.put("imsi", "111");
        deviceJson.put("imei","111");
        deviceJson.put("title", "1111");
        ResponseEntity<JsonResult> forEntity = restTemplate.postForEntity(cmccNetUrl + "/addDevice", deviceJson, JsonResult.class);
        System.out.println(forEntity.getBody());
    }

    public static void main(String[] args)
    {

        File file = FileUtils.getFile("E:\\IdeaProjects\\YYDS\\AppResources\\sn\\MINI201904180001\\123");
        System.out.println(file.length());
    }

}
