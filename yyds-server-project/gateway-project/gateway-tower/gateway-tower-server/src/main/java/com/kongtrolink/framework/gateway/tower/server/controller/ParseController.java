package com.kongtrolink.framework.gateway.tower.server.controller;

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


    @RequestMapping("/testParse")
    public void TestParse(@RequestBody(required = false) String payload) {

    }




}
