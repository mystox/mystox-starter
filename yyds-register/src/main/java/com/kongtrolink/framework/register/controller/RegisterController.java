package com.kongtrolink.framework.register.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.config.WebPrivFuncConfig;
import com.kongtrolink.framework.core.IaContext;
import com.kongtrolink.framework.core.IaENV;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.OperaResult;
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
   @Autowired
   IaContext iaContext;
   Logger logger = LoggerFactory.getLogger(RegisterController.class);


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



   @RequestMapping("/getPath")
   public JsonResult getZookeeper(@RequestParam(required = false) String path) {
       IaENV iaENV =iaContext.getIaENV();
       String sessionId = iaENV.getConf().getMyId();
       JSONObject result = new JSONObject();
       result.put("zookeeperSession", sessionId);
       List<String> children = null;
       try {
           String data = iaENV.getRegScheduler().getData(path);
           logger.info("data: [{}]", data);
           result.put("data", JSON.parse(data));
           children = iaENV.getRegScheduler().getChildren(path);
           result.put("children", children);
       }  catch (Exception e) {
           e.printStackTrace();
       }
       return new JsonResult(result);
   }


   @RequestMapping("/refreshWebConfig")
   public JsonResult testConfigRefresh() {
       IaENV iaENV =iaContext.getIaENV();
       Object invoke = genericPostableMvcEndpoint.invoke();
       logger.info(JSONObject.toJSONString(invoke));
       OperaResult operaResult = null;
       try {
           iaENV.getRegScheduler().registerWebPriv(iaENV.getConf().getWebPrivFuncConfig());
       } catch (Exception e) {
           e.printStackTrace();
       }
       return new JsonResult("");
   }
}
