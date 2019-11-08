package com.kongtrolink.framework.log.controller;

import com.kongtrolink.framework.entity.JsonResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mystoxlol on 2019/11/8, 14:42.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/log")
public class LogController {

    @RequestMapping("/getMqttLog")
    public JsonResult getMqttLog() {

        return new JsonResult();
    }

}
