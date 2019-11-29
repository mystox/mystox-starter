package com.kongtrolink.framework.foo.controller;

import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.foo.api.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/22 10:31
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/foo")
public class FooController {
    @Autowired
    ThreadPoolTaskExecutor mqttExecutor;
    @Autowired
    PerformanceService performanceService;

    @RequestMapping("/getCount")
    public String getCount() {
        long count = performanceService.getCount();
        return "统计数量： " + count;
    }

    @RequestMapping("/clearCount")
    public String clearCount() {
        long l = performanceService.clearCount("");
        return "清除统计数量： " + l;
    }

    @RequestMapping("/getTaskPoolMsg")
    public JsonResult getTaskPoolMsg() {
        int corePoolSize = mqttExecutor.getCorePoolSize();
        int poolSize = mqttExecutor.getPoolSize();
        int activeCount = mqttExecutor.getActiveCount();
        int maxPoolSize = mqttExecutor.getMaxPoolSize();
        return new JsonResult("corePoolSize"+corePoolSize+"\n poolSize"+poolSize+"\n activeCount"+activeCount+"\n maxPoolSize"+maxPoolSize);
    }

}