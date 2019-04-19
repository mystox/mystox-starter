package com.kongtrolink.framework;

import com.kongtrolink.framework.core.ControllerInstance;
import com.kongtrolink.framework.dao.FsuDevicesDao;
import com.kongtrolink.framework.model.signal.SignalCode;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YyjwAppServerApplicationTests {
    Logger logger = LoggerFactory.getLogger("~");
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    FsuDevicesDao fsuDevicesDao;

    @Test
    public void contextLoads() {
        logger.info("初始化信号点code... ...");
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
                   /* for (String signalString : result) {
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
                    }*/
                }
                if (file.getName().startsWith("signal")) {
                    List<String> result = FileUtils.readLines(file, Charset.forName("UTF-8").toString());
                    for (String signalString : result) {
                        if (signalString == null || !signalString.contains(","))
                            continue;
                        String[] signalStrArr = signalString.split(",");

                        /*SignalCode signalCode = new SignalCode();
                        if (signalStrArr.length > 9) {
                            signalCode.setUnit(signalStrArr[9]);
                        }
                        signalCode.setName(signalStrArr[3]);
                        String signalId = signalStrArr[1];
                        signalMap.put(signalId, signalCode);*/
                    }
                }
            }
            ControllerInstance.getInstance().setSignalMap(signalMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
