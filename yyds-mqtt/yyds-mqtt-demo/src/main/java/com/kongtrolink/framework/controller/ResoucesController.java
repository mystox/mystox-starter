package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/8/27, 9:24.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/resources")
public class ResoucesController {

    @RequestMapping("/updateJarRes")
    public String updateJarResources() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);

        try {
            File file = FileUtils.getFile("jarResources/jarRes.yml");
            System.out.println(file.getAbsolutePath());
            Map load = (Map) yaml.load(new FileInputStream(file));
            load.put("operaCode3", "abcddd");
            System.out.println(JSONObject.toJSONString(load));

            yaml.dump(load,new FileWriter(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }


}
