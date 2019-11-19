package com.kongtrolink.framework.register.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.OperaResult;
import com.kongtrolink.framework.register.config.WebPrivFuncConfig;
import com.kongtrolink.framework.register.runner.RegisterRunner;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.endpoint.GenericPostableMvcEndpoint;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mystoxlol on 2019/8/29, 9:46.
 * company: kongtrolink
 * description:
 * update record:
 */
@Lazy
@RestController
@RequestMapping("/register")
public class RegisterController {
    Logger logger = LoggerFactory.getLogger(RegisterController.class);
    ServiceRegistry serviceRegistry;

    @Autowired
    public void setZooKeeper(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    private GenericPostableMvcEndpoint genericPostableMvcEndpoint;

    @Autowired
    @Lazy
    private void setGenericPostableMvcEndpoint(GenericPostableMvcEndpoint genericPostableMvcEndpoint) {
        this.genericPostableMvcEndpoint = genericPostableMvcEndpoint;
    }

    WebPrivFuncConfig webPrivFuncConfig;

    @Autowired
    public void setWebPrivFuncConfig(WebPrivFuncConfig webPrivFuncConfig) {
        this.webPrivFuncConfig = webPrivFuncConfig;
    }

    @Autowired
    @Lazy
    RegisterRunner registerRunner;

    @RequestMapping("/getPath")
    public JsonResult getZookeeper(@RequestParam(required = false) String path) {
        long sessionId = serviceRegistry.getZk().getSessionId();
        JSONObject result = new JSONObject();
        result.put("zookeeperSession", sessionId);
        List<String> children = null;
        try {
            String data = serviceRegistry.getData(path);
            logger.info("data: [{}]", data);
            result.put("data", JSON.parse(data));
            children = serviceRegistry.getChildren(path);
            result.put("children", children);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsonResult(result);
    }


    @RequestMapping("/refreshWebConfig")
    public JsonResult testConfigRefresh() {
        Object invoke = genericPostableMvcEndpoint.invoke();
        logger.info(JSONObject.toJSONString(invoke));
        OperaResult operaResult = registerRunner.registerWebPriv();
        return new JsonResult(operaResult);
    }


}
