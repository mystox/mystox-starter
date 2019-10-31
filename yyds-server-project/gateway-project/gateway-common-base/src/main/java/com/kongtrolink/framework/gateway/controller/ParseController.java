package com.kongtrolink.framework.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.service.parse.ParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/10/21, 16:00.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/parse")
public class ParseController {


    @Autowired
    ParseService parseService;

    @RequestMapping("/testParse")
    public void TestParse(@RequestBody(required = false) String payload) {
        parseService.execute(payload);
    }


    public static void main(String[] args)
    {
        ParseProtocol parseProtocol = new ParseProtocol();
        parseProtocol = new ParseProtocol("PushAlarm","123","mqtt","ddddddddddd");
        System.out.println(JSONObject.toJSONString(parseProtocol));
    }

}
