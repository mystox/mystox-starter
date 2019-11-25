package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.dao.impl.Neo4jDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/Test")
public class TestController {

    @Autowired
    Neo4jDBService neo4jDBService;

    @Resource(name = "assetManagementExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @RequestMapping("/search")
    public String testSearchCI(@RequestBody JSONObject requestBody) {

        int threadCount = requestBody.getInteger("threadCount");
        final String enterpriseCode = requestBody.getString("enterpriseCode");
        final String serverCode = requestBody.getString("serverCode");
        final int times = requestBody.getInteger("times");
        final int pageNum = requestBody.getInteger("pageNum");

        Boolean[] threadBoolean = new Boolean[threadCount];

        neo4jDBService.searchCIConnectionType();

        for (int i = 0; i < threadCount; ++i) {
            threadBoolean[i] = false;
            final int index = i;
            taskExecutor.execute(()->searchCI(index, enterpriseCode, serverCode, times, pageNum));
        }

        return "1";
    }

    private void searchCI(int index, String enterpriseCode, String serverCode, int times, int pageNum) {

        int pageTotal = 60000 / pageNum;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", enterpriseCode);
        jsonObject.put("serverCode", serverCode);
        jsonObject.put("pageNum", pageNum);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        System.out.println(simpleDateFormat.format(now) + ":Thread-" + index + " Start");

        long sum = 0;
        for (int count = 0; count < times; ++count) {
            for (int i = 1; i <= pageTotal; ++i) {
                jsonObject.put("curPage", i);

                Date startDate = new Date();
                neo4jDBService.searchCI(jsonObject);
                Date endDate = new Date();

                long diff = endDate.getTime() - startDate.getTime();

                System.out.println(simpleDateFormat.format(startDate) + ":Thread-" + index + ",count-" + count + ",i-" + i + ",Begin");
                System.out.println(simpleDateFormat.format(endDate) + ":Thread-" + index + ",count-" + count + ",i-" + i + ",End,interval-" + diff);

                sum += diff;
            }
        }

        double average = sum * 1.0 / times / pageTotal;
        System.out.println("Thread-" + index + ",average-" + average);
    }
}
