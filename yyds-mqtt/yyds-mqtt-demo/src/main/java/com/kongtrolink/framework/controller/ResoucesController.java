package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.config.OperaRouteConfigTest;
import com.kongtrolink.framework.register.config.OperaRouteConfig;
import com.kongtrolink.framework.register.config.WebPrivFuncConfig;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.endpoint.GenericPostableMvcEndpoint;
import org.springframework.context.annotation.Lazy;
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


    Logger logger = LoggerFactory.getLogger(ResoucesController.class);
    @Autowired
    OperaRouteConfigTest operaRouteConfigTest;


    @RequestMapping("/updateJarRes")
    public String updateJarResources() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);

        try {
            File file = FileUtils.getFile("jarResources/jarRes.yml");
            Map load = (Map) yaml.load(new FileInputStream(file));
            yaml.dump(load,new FileWriter(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }
    private GenericPostableMvcEndpoint genericPostableMvcEndpoint;

    @Autowired
    @Lazy
    private void setGenericPostableMvcEndpoint(GenericPostableMvcEndpoint genericPostableMvcEndpoint) {
        this.genericPostableMvcEndpoint = genericPostableMvcEndpoint;
    }

    @Autowired
    WebPrivFuncConfig webPrivFuncConfig;
    @Autowired
    OperaRouteConfig operaRouteConfig;
    @RequestMapping("/updateOperaRoute")
    public String updateOperaRoute() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);
        OperaRouteConfigTest test = new OperaRouteConfigTest();
//        test.setOpoeraResource(map);
        try {
            File file = FileUtils.getFile("./config/operaRoute-test-1.yml");
            Map testLoad = (Map) yaml.load(new FileInputStream(file));
//            yaml.dump(JSONObject.toJSON(test),new FileWriter(file));
//            Object invoke = genericPostableMvcEndpoint.invoke();
//            logger.info(JSONObject.toJSONString(invoke));
            System.out.println(JSONObject.toJSONString(operaRouteConfig.getOperaRoute()));
            System.out.println(JSONObject.toJSONString(webPrivFuncConfig.getPrivFunc()));
            return JSONObject.toJSONString(testLoad);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }





}
