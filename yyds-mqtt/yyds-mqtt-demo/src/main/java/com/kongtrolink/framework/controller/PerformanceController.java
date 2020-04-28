package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.IaContext;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.StateCode;
import com.kongtrolink.framework.service.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/22 10:38
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/performance")
public class PerformanceController {

    Logger logger = LoggerFactory.getLogger(PerformanceController.class);
//    @Autowired
//    MqttSender mqttSender;

    @Autowired
    IaContext iaContext;

    @Autowired
    ThreadPoolTaskExecutor demoExecutor;
    private boolean breakFlag = false;


    @RequestMapping("/msgSendLogic")
    public JsonResult msgSendLogic(@RequestBody JSONObject condition) {
        Long count = condition.getLong("count");
        int qos = condition.getInteger("qos");
        long timeSeconds = condition.getLong("timeSeconds");
        Integer intensity = condition.getInteger("intensity"); //密集度
        Integer coreSize = condition.getInteger("coreSize");

        if (intensity == null) {
            intensity = 1;
        }
        final int intensityF = intensity;
        Boolean aBreak = condition.getBoolean("break");
        Boolean syn = condition.getBoolean("syn");
        if (aBreak) {
            breakFlag = true;
            return new JsonResult("中断");
        } else {
            breakFlag = false;
        }

        long time = 1000 / timeSeconds;

//        MsgResult countStatistic = mqttSender.sendToMqttSync("FOO_SERVER_DEMO_1.0.0", "clearCount", "");
//        MsgResult countStatistic = new MsgResult();
        String baseMsg = condition.getString("msg");
        if (coreSize!= null) demoExecutor.setCorePoolSize(coreSize);
        demoExecutor.execute(() -> {
            LongAdder longAdder = new LongAdder();
            for (long i = 0; i < count; i++) {
                if (breakFlag) {
                    logger.warn("task break count is {}", i);
                    return;
                }
                if (i % 1000 == 0) {
                    int poolSize = demoExecutor.getPoolSize();
                    int activeCount = demoExecutor.getActiveCount();
                    int corePoolSize = demoExecutor.getCorePoolSize();
                    logger.info("count: {}, pool size: {} active count: {}, core pool size: {}", i * intensityF, poolSize, activeCount, corePoolSize);
                }
                String msg = baseMsg + i;
                MsgHandler msgHandler = iaContext.getIaENV().getMsgScheudler().getIahander();
                demoExecutor.execute(() -> {
                    for (int j = 0; j < intensityF; j++)
                        if (syn != null && !syn)
                            msgHandler.sendToMqtt("FOO_SERVER_DEMO_1.0.0", "countStatistic", qos, msg);
                        else {
                            MsgResult countStatistic1 = msgHandler.
                                    sendToMqttSync("FOO_SERVER_DEMO_1.0.0", "countStatistic", qos,msg,100, TimeUnit.SECONDS);
                            if (countStatistic1 != null && StateCode.SUCCESS != countStatistic1.getStateCode()) {
                                logger.error("send error...");
                            } else
                            {
                                longAdder.add(1);
                                long l = longAdder.longValue();
                                if (l % 1000 ==0)
                                    logger.info("ack count: {}",l);
                            }
                        }
                });
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            logger.info("send ending...");
        });
        return new JsonResult("totalCount is: " + count * intensity + " lastTime count is: " /*+ countStatistic.getMsg()*/);
    }



    @RequestMapping("/createThread")
    public JsonResult createThread(@RequestBody JSONObject condition)
    {
        Long count = condition.getLong("count");
        Long sleepTime = condition.getLong("sleepTime");
        for (int i = 0; i < count; i++) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            );
            t.setName("demo-create"+i);
            demoExecutor.submit(t);
        }
            return new JsonResult();
    }

}