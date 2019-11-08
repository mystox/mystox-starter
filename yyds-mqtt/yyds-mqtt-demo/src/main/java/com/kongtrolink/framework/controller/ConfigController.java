package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.register.config.WebPrivFuncConfig;
import com.kongtrolink.framework.register.entity.PrivFuncEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/11/6, 20:50.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/configTest")
@RefreshScope
public class ConfigController {


    @Autowired
    WebPrivFuncConfig webPrivFuncConfig;
    @RequestMapping("/getWebPrivFuncTest")
    public JsonResult getWebPrivFuncTest() {
        PrivFuncEntity privFuncEntity = webPrivFuncConfig.getPrivFunc();
        return new JsonResult(JSONObject.parseObject(JSONObject.toJSONString(privFuncEntity)));
    }
}
