package tech.mystox.framework.foo.controller;

import com.alibaba.fastjson2.JSONObject;
import tech.mystox.framework.api.test.PerformanceService;
import tech.mystox.framework.entity.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/22 10:31
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/foo")
public class FooController {
    @Autowired(required = false)
    ThreadPoolTaskExecutor mqttExecutor;
    @Autowired
    PerformanceService performanceService;

    @RequestMapping("/getCount")
    public String getCount() {
        Map<String,Long> count = performanceService.getCount();
        return "统计数量： " + JSONObject.toJSONString(count);
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


    @RequestMapping("/importFile")
    public JsonResult importFile(@RequestParam MultipartFile file) {
        System.out.println(file.getOriginalFilename());
        return new JsonResult();
    }

}