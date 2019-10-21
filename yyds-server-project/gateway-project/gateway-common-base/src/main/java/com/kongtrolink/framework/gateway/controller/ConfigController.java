package com.kongtrolink.framework.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.gateway.entity.TransverterConfig;
import com.kongtrolink.framework.gateway.service.parse.ParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.endpoint.GenericPostableMvcEndpoint;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/10/21, 14:18.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping(value = "/config")
public class ConfigController {

    @Autowired
    TransverterConfig transverterConfig;
    @Value("${server.port}")
    private int port;
    @Autowired
    ParseService parseService;


    @Autowired
    @Lazy
    GenericPostableMvcEndpoint genericPostableMvcEndpoint;
    @RequestMapping("/refreshConfig")
    public String testConfigRefresh() {
        genericPostableMvcEndpoint.invoke();
        parseService.configTransverterInit();
        return JSONObject.toJSONString(transverterConfig);
    }




}
