package com.kongtrolink.framework.register.runner;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.AckEnum;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.entity.UnitHead;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mystoxlol on 2019/11/6, 20:05.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class WebResourceScanner {
    private Logger logger = LoggerFactory.getLogger(WebResourceScanner.class);

    @Value("${jarResources.path:./configResources}")
    private String configResPath;


    @Value("${server.name}_${server.version}")
    private String serverCode;

    public List<RegisterSub> scannerWebResources() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);
        File file = FileUtils.getFile(configResPath + "/config/privFuncConfig.yml");
        List<RegisterSub> subList = new ArrayList<>();
        if (file.exists()) {
            try {
                Map load = (Map) yaml.load(new FileInputStream(file));
                Map<String, String> operaMap = (Map<String, String>) load.get(serverCode);
                if (!CollectionUtils.isEmpty(operaMap)) {
                    Set<Map.Entry<String, String>> entries = operaMap.entrySet();
                    for (Map.Entry<String, String> e : entries) {
                        RegisterSub sub = new RegisterSub();
                        String value = e.getValue();
                        String key = e.getKey();
                        String[] split = value.split(":");
                        String executeUnit = UnitHead.JAR + split[0];
                        sub.setAck(split.length > 1 && AckEnum.ACK.toString().equals(split[1]) ?
                                AckEnum.ACK : AckEnum.NA);
                        sub.setOperaCode(key);
                        sub.setExecuteUnit(executeUnit);
                        subList.add(sub);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.info("jar scanner result: [{}]", JSONObject.toJSONString(subList));
        return subList;
    }
}
